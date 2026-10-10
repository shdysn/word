package com.prowrite.editor.ui.editor

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.prowrite.editor.core.datastore.PreferencesManager
import com.prowrite.editor.core.model.Document
import com.prowrite.editor.core.model.FindReplaceState
import com.prowrite.editor.core.model.FormattingState
import com.prowrite.editor.core.model.HeadingLevel
import com.prowrite.editor.core.model.SaveStatus
import com.prowrite.editor.core.model.TextAlignment
import com.prowrite.editor.domain.repository.DocumentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditorUiState(
    val document: Document? = null,
    val title: String = "Untitled Document",
    val textFieldValue: TextFieldValue = TextFieldValue(""),
    val saveStatus: SaveStatus = SaveStatus.SAVED,
    val formatting: FormattingState = FormattingState(),
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val wordCount: Int = 0,
    val characterCount: Int = 0,
    val readingTimeMin: Int = 1,
    val findReplace: FindReplaceState = FindReplaceState(),
    val showRenameDialog: Boolean = false,
    val showDetailsSheet: Boolean = false,
    val userMessage: String? = null
)

class EditorViewModel(
    private val documentId: Long,
    private val repository: DocumentRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<String>()
    private val redoStack = mutableListOf<String>()

    private var autoSaveJob: Job? = null
    private var autoSaveIntervalMs: Long = 3000L

    init {
        loadDocument()
        viewModelScope.launch {
            val prefs = preferencesManager.preferencesFlow.first()
            autoSaveIntervalMs = prefs.autoSaveIntervalSec * 1000L
        }
    }

    private fun loadDocument() {
        viewModelScope.launch {
            val doc = repository.getDocumentById(documentId)
            if (doc != null) {
                undoStack.clear()
                redoStack.clear()
                undoStack.add(doc.content)

                val words = calculateWords(doc.content)
                _uiState.update {
                    it.copy(
                        document = doc,
                        title = doc.title,
                        textFieldValue = TextFieldValue(doc.content, TextRange(doc.content.length)),
                        saveStatus = SaveStatus.SAVED,
                        wordCount = words,
                        characterCount = doc.content.length,
                        readingTimeMin = maxOf(1, words / 200)
                    )
                }
            }
        }
    }

    fun onContentChange(newVal: TextFieldValue) {
        val oldContent = _uiState.value.textFieldValue.text
        val newContent = newVal.text

        if (oldContent != newContent) {
            if (undoStack.isEmpty() || undoStack.last() != oldContent) {
                undoStack.add(oldContent)
                if (undoStack.size > 50) undoStack.removeAt(0)
            }
            redoStack.clear()
        }

        val words = calculateWords(newContent)
        _uiState.update {
            it.copy(
                textFieldValue = newVal,
                saveStatus = SaveStatus.UNSAVED_CHANGES,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty(),
                wordCount = words,
                characterCount = newContent.length,
                readingTimeMin = maxOf(1, words / 200)
            )
        }

        scheduleAutoSave()
    }

    fun onTitleChange(newTitle: String) {
        if (newTitle.isNotBlank()) {
            _uiState.update { it.copy(title = newTitle, saveStatus = SaveStatus.UNSAVED_CHANGES) }
            scheduleAutoSave()
        }
    }

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            _uiState.update { it.copy(saveStatus = SaveStatus.SAVING) }
            delay(autoSaveIntervalMs)
            saveDocumentInternal()
        }
    }

    fun saveImmediately() {
        autoSaveJob?.cancel()
        viewModelScope.launch {
            saveDocumentInternal()
        }
    }

    private suspend fun saveDocumentInternal() {
        val currentDoc = _uiState.value.document ?: return
        val currentTitle = _uiState.value.title
        val currentContent = _uiState.value.textFieldValue.text

        val updated = currentDoc.copy(
            title = currentTitle,
            content = currentContent,
            plainText = currentContent,
            updatedAt = System.currentTimeMillis()
        )

        repository.updateDocument(updated)
        _uiState.update {
            it.copy(
                document = updated,
                saveStatus = SaveStatus.SAVED
            )
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val currentContent = _uiState.value.textFieldValue.text
            redoStack.add(currentContent)

            val previous = undoStack.removeAt(undoStack.lastIndex)
            val words = calculateWords(previous)
            _uiState.update {
                it.copy(
                    textFieldValue = TextFieldValue(previous, TextRange(previous.length)),
                    canUndo = undoStack.isNotEmpty(),
                    canRedo = true,
                    wordCount = words,
                    characterCount = previous.length,
                    saveStatus = SaveStatus.UNSAVED_CHANGES
                )
            }
            scheduleAutoSave()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val currentContent = _uiState.value.textFieldValue.text
            undoStack.add(currentContent)

            val next = redoStack.removeAt(redoStack.lastIndex)
            val words = calculateWords(next)
            _uiState.update {
                it.copy(
                    textFieldValue = TextFieldValue(next, TextRange(next.length)),
                    canUndo = true,
                    canRedo = redoStack.isNotEmpty(),
                    wordCount = words,
                    characterCount = next.length,
                    saveStatus = SaveStatus.UNSAVED_CHANGES
                )
            }
            scheduleAutoSave()
        }
    }

    // Formatting insertion/wrapping
    fun applyFormatting(prefix: String, suffix: String = prefix) {
        val current = _uiState.value.textFieldValue
        val text = current.text
        val selection = current.selection

        val newText: String
        val newSelection: TextRange

        if (selection.collapsed) {
            // Insert formatting placeholders
            newText = text.substring(0, selection.start) + "$prefix$suffix" + text.substring(selection.start)
            newSelection = TextRange(selection.start + prefix.length)
        } else {
            // Wrap selected text
            val selectedPart = text.substring(selection.min, selection.max)
            val formatted = "$prefix$selectedPart$suffix"
            newText = text.substring(0, selection.min) + formatted + text.substring(selection.max)
            newSelection = TextRange(selection.min + prefix.length, selection.min + prefix.length + selectedPart.length)
        }

        onContentChange(TextFieldValue(newText, newSelection))
    }

    fun applyHeading(level: HeadingLevel) {
        val current = _uiState.value.textFieldValue
        val text = current.text
        val prefix = when (level) {
            HeadingLevel.NORMAL -> ""
            HeadingLevel.TITLE -> "# "
            HeadingLevel.HEADING_1 -> "## "
            HeadingLevel.HEADING_2 -> "### "
            HeadingLevel.HEADING_3 -> "#### "
        }

        val cursor = current.selection.start
        val lineStart = text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)).let { if (it == -1) 0 else it + 1 }
        val lineEnd = text.indexOf('\n', cursor).let { if (it == -1) text.length else it }
        val currentLine = text.substring(lineStart, lineEnd)

        val strippedLine = currentLine.replace("^#+\\s*".toRegex(), "")
        val updatedLine = if (prefix.isEmpty()) strippedLine else "$prefix$strippedLine"

        val newText = text.substring(0, lineStart) + updatedLine + text.substring(lineEnd)
        onContentChange(TextFieldValue(newText, TextRange(lineStart + updatedLine.length)))
    }

    fun applyList(isNumbered: Boolean) {
        val current = _uiState.value.textFieldValue
        val text = current.text
        val cursor = current.selection.start

        val lineStart = text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)).let { if (it == -1) 0 else it + 1 }
        val lineEnd = text.indexOf('\n', cursor).let { if (it == -1) text.length else it }
        val currentLine = text.substring(lineStart, lineEnd)

        val prefix = if (isNumbered) "1. " else "- "
        val updatedLine = if (currentLine.startsWith(prefix)) {
            currentLine.removePrefix(prefix)
        } else {
            "$prefix$currentLine"
        }

        val newText = text.substring(0, lineStart) + updatedLine + text.substring(lineEnd)
        onContentChange(TextFieldValue(newText, TextRange(lineStart + updatedLine.length)))
    }

    fun clearFormatting() {
        val current = _uiState.value.textFieldValue
        val text = current.text
        val selection = current.selection
        if (!selection.collapsed) {
            val selectedPart = text.substring(selection.min, selection.max)
            val cleaned = selectedPart.replace("[*_~#`>\\[\\]]".toRegex(), "")
            val newText = text.substring(0, selection.min) + cleaned + text.substring(selection.max)
            onContentChange(TextFieldValue(newText, TextRange(selection.min, selection.min + cleaned.length)))
        }
    }

    // --- Find and Replace ---

    fun toggleFindReplace() {
        _uiState.update {
            val active = !it.findReplace.isActive
            it.copy(findReplace = it.findReplace.copy(isActive = active))
        }
    }

    fun updateFindQuery(query: String) {
        val content = _uiState.value.textFieldValue.text
        val matches = if (query.isNotBlank()) {
            val indices = mutableListOf<Int>()
            var index = content.indexOf(query, 0, ignoreCase = true)
            while (index != -1) {
                indices.add(index)
                index = content.indexOf(query, index + query.length, ignoreCase = true)
            }
            indices
        } else {
            emptyList()
        }

        _uiState.update {
            it.copy(
                findReplace = it.findReplace.copy(
                    findQuery = query,
                    matchIndices = matches,
                    currentMatchIndex = if (matches.isNotEmpty()) 0 else -1
                )
            )
        }
    }

    fun updateReplaceQuery(query: String) {
        _uiState.update {
            it.copy(findReplace = it.findReplace.copy(replaceQuery = query))
        }
    }

    fun findNext() {
        val fr = _uiState.value.findReplace
        if (fr.matchIndices.isNotEmpty()) {
            val next = (fr.currentMatchIndex + 1) % fr.matchIndices.size
            _uiState.update { it.copy(findReplace = fr.copy(currentMatchIndex = next)) }
            val index = fr.matchIndices[next]
            _uiState.update {
                it.copy(
                    textFieldValue = it.textFieldValue.copy(
                        selection = TextRange(index, index + fr.findQuery.length)
                    )
                )
            }
        }
    }

    fun findPrevious() {
        val fr = _uiState.value.findReplace
        if (fr.matchIndices.isNotEmpty()) {
            val prev = if (fr.currentMatchIndex - 1 < 0) fr.matchIndices.size - 1 else fr.currentMatchIndex - 1
            _uiState.update { it.copy(findReplace = fr.copy(currentMatchIndex = prev)) }
            val index = fr.matchIndices[prev]
            _uiState.update {
                it.copy(
                    textFieldValue = it.textFieldValue.copy(
                        selection = TextRange(index, index + fr.findQuery.length)
                    )
                )
            }
        }
    }

    fun replaceCurrent() {
        val fr = _uiState.value.findReplace
        if (fr.matchIndices.isNotEmpty() && fr.currentMatchIndex >= 0) {
            val index = fr.matchIndices[fr.currentMatchIndex]
            val text = _uiState.value.textFieldValue.text
            val newText = text.substring(0, index) + fr.replaceQuery + text.substring(index + fr.findQuery.length)
            onContentChange(TextFieldValue(newText, TextRange(index + fr.replaceQuery.length)))
            updateFindQuery(fr.findQuery)
        }
    }

    fun replaceAll() {
        val fr = _uiState.value.findReplace
        if (fr.findQuery.isNotBlank()) {
            val text = _uiState.value.textFieldValue.text
            val newText = text.replace(fr.findQuery, fr.replaceQuery, ignoreCase = true)
            onContentChange(TextFieldValue(newText, TextRange(0)))
            updateFindQuery(fr.findQuery)
        }
    }

    private fun calculateWords(content: String): Int {
        return content.trim().split("\\s+".toRegex()).count { it.isNotBlank() }
    }

    fun showRenameDialog(show: Boolean) {
        _uiState.update { it.copy(showRenameDialog = show) }
    }

    fun showDetailsSheet(show: Boolean) {
        _uiState.update { it.copy(showDetailsSheet = show) }
    }

    companion object {
        fun provideFactory(
            documentId: Long,
            repository: DocumentRepository,
            preferencesManager: PreferencesManager
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EditorViewModel(documentId, repository, preferencesManager) as T
            }
        }
    }
}
