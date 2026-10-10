package com.prowrite.editor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.prowrite.editor.core.datastore.AppPreferences
import com.prowrite.editor.core.datastore.DocumentLayoutMode
import com.prowrite.editor.core.datastore.DocumentSortOption
import com.prowrite.editor.core.datastore.PreferencesManager
import com.prowrite.editor.core.model.Document
import com.prowrite.editor.core.model.DocumentTemplate
import com.prowrite.editor.core.model.Folder
import com.prowrite.editor.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val documents: List<Document> = emptyList(),
    val recentDocuments: List<Document> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val selectedTab: Int = 0, // 0 = All, 1 = Recent, 2 = Favorites
    val layoutMode: DocumentLayoutMode = DocumentLayoutMode.LIST,
    val sortOption: DocumentSortOption = DocumentSortOption.MODIFIED_DESC,
    val selectedDocumentIds: Set<Long> = emptySet(),
    val isSelectionMode: Boolean = false,
    val activeFolderFilter: Long? = null,
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

private data class FilterState(
    val tab: Int,
    val selectedIds: Set<Long>,
    val folderFilter: Long?,
    val userMessage: String?
)

private data class DbData(
    val allDocs: List<Document>,
    val recentDocs: List<Document>,
    val folders: List<Folder>
)

class HomeViewModel(
    private val repository: DocumentRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val selectedTabFlow = MutableStateFlow(0)
    private val selectedIdsFlow = MutableStateFlow<Set<Long>>(emptySet())
    private val folderFilterFlow = MutableStateFlow<Long?>(null)
    private val userMessageFlow = MutableStateFlow<String?>(null)

    private val dbDataFlow = combine(
        repository.observeAllDocuments(),
        repository.observeRecentDocuments(8),
        repository.observeFolders()
    ) { all, recent, folders ->
        DbData(all, recent, folders)
    }

    private val filterStateFlow = combine(
        selectedTabFlow,
        selectedIdsFlow,
        folderFilterFlow,
        userMessageFlow
    ) { tab, ids, folderFilter, msg ->
        FilterState(tab, ids, folderFilter, msg)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        dbDataFlow,
        preferencesManager.preferencesFlow,
        filterStateFlow
    ) { dbData: DbData, prefs: AppPreferences, filter: FilterState ->

        val filteredByFolder = if (filter.folderFilter != null) {
            dbData.allDocs.filter { it.folderId == filter.folderFilter }
        } else {
            dbData.allDocs
        }

        val filteredByTab = when (filter.tab) {
            0 -> filteredByFolder
            1 -> dbData.recentDocs
            2 -> filteredByFolder.filter { it.isFavorite }
            else -> filteredByFolder
        }

        val sortedDocs = when (prefs.sortOption) {
            DocumentSortOption.MODIFIED_DESC -> filteredByTab.sortedByDescending { it.updatedAt }
            DocumentSortOption.MODIFIED_ASC -> filteredByTab.sortedBy { it.updatedAt }
            DocumentSortOption.TITLE_ASC -> filteredByTab.sortedBy { it.title.lowercase() }
            DocumentSortOption.TITLE_DESC -> filteredByTab.sortedByDescending { it.title.lowercase() }
            DocumentSortOption.WORD_COUNT_DESC -> filteredByTab.sortedByDescending { it.wordCount }
        }

        HomeUiState(
            documents = sortedDocs,
            recentDocuments = dbData.recentDocs,
            folders = dbData.folders,
            selectedTab = filter.tab,
            layoutMode = prefs.layoutMode,
            sortOption = prefs.sortOption,
            selectedDocumentIds = filter.selectedIds,
            isSelectionMode = filter.selectedIds.isNotEmpty(),
            activeFolderFilter = filter.folderFilter,
            userMessage = filter.userMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun selectTab(tabIndex: Int) {
        selectedTabFlow.value = tabIndex
    }

    fun setFolderFilter(folderId: Long?) {
        folderFilterFlow.value = folderId
    }

    fun toggleLayoutMode() {
        viewModelScope.launch {
            val current = uiState.value.layoutMode
            val next = if (current == DocumentLayoutMode.LIST) DocumentLayoutMode.GRID else DocumentLayoutMode.LIST
            preferencesManager.setLayoutMode(next)
        }
    }

    fun setSortOption(option: DocumentSortOption) {
        viewModelScope.launch {
            preferencesManager.setSortOption(option)
        }
    }

    fun toggleSelection(documentId: Long) {
        val current = selectedIdsFlow.value
        selectedIdsFlow.value = if (current.contains(documentId)) {
            current - documentId
        } else {
            current + documentId
        }
    }

    fun clearSelection() {
        selectedIdsFlow.value = emptySet()
    }

    fun selectAll() {
        selectedIdsFlow.value = uiState.value.documents.map { it.id }.toSet()
    }

    fun createBlankDocument(onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createDocument("Untitled Document", "")
            onCreated(id)
        }
    }

    fun createFromTemplate(template: DocumentTemplate, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createDocument(template.title, template.initialContent)
            onCreated(id)
        }
    }

    fun duplicateDocument(documentId: Long) {
        viewModelScope.launch {
            repository.duplicateDocument(documentId)
            userMessageFlow.value = "Document duplicated"
        }
    }

    fun renameDocument(documentId: Long, newTitle: String) {
        viewModelScope.launch {
            val doc = repository.getDocumentById(documentId) ?: return@launch
            repository.updateDocument(doc.copy(title = newTitle))
            userMessageFlow.value = "Document renamed"
        }
    }

    fun toggleFavorite(document: Document) {
        viewModelScope.launch {
            repository.toggleFavorite(document.id, !document.isFavorite)
        }
    }

    fun toggleArchive(document: Document) {
        viewModelScope.launch {
            repository.toggleArchive(document.id, !document.isArchived)
            userMessageFlow.value = if (document.isArchived) "Document restored from archive" else "Document archived"
        }
    }

    fun moveDocument(documentId: Long, folderId: Long?) {
        viewModelScope.launch {
            repository.moveDocumentToFolder(documentId, folderId)
            userMessageFlow.value = "Document moved"
        }
    }

    fun deleteDocument(documentId: Long) {
        viewModelScope.launch {
            repository.deleteDocument(documentId)
            userMessageFlow.value = "Document moved to Recycle Bin"
        }
    }

    fun deleteSelectedDocuments() {
        viewModelScope.launch {
            val ids = selectedIdsFlow.value
            ids.forEach { repository.deleteDocument(it) }
            selectedIdsFlow.value = emptySet()
            userMessageFlow.value = "${ids.size} documents moved to Recycle Bin"
        }
    }

    fun clearUserMessage() {
        userMessageFlow.value = null
    }

    companion object {
        fun provideFactory(
            repository: DocumentRepository,
            preferencesManager: PreferencesManager
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository, preferencesManager) as T
            }
        }
    }
}
