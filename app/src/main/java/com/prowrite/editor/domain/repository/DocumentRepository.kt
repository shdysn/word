package com.prowrite.editor.domain.repository

import com.prowrite.editor.core.database.ProWriteDatabase
import com.prowrite.editor.core.model.Document
import com.prowrite.editor.core.model.Folder
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    suspend fun createDocument(title: String, content: String = "", folderId: Long? = null): Long
    suspend fun updateDocument(document: Document)
    suspend fun getDocumentById(id: Long): Document?
    suspend fun duplicateDocument(id: Long): Long?
    suspend fun deleteDocument(id: Long)
    suspend fun restoreDocument(id: Long)
    suspend fun permanentlyDeleteDocument(id: Long)
    suspend fun emptyRecycleBin()
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
    suspend fun toggleArchive(id: Long, isArchived: Boolean)
    suspend fun moveDocumentToFolder(id: Long, folderId: Long?)
    suspend fun searchDocuments(query: String): List<Document>

    fun observeAllDocuments(): Flow<List<Document>>
    fun observeRecentDocuments(limit: Int = 10): Flow<List<Document>>
    fun observeFavorites(): Flow<List<Document>>
    fun observeArchived(): Flow<List<Document>>
    fun observeRecycleBin(): Flow<List<Document>>
    fun observeFolderDocuments(folderId: Long): Flow<List<Document>>

    // Folders
    suspend fun createFolder(name: String, colorHex: String): Long
    suspend fun updateFolder(id: Long, name: String, colorHex: String)
    suspend fun deleteFolder(id: Long)
    fun observeFolders(): Flow<List<Folder>>

    // Recent Searches
    suspend fun recordSearch(query: String)
    suspend fun getRecentSearches(): List<String>
    suspend fun deleteSearch(query: String)
    suspend fun clearSearches()
}

class DocumentRepositoryImpl(
    private val database: ProWriteDatabase
) : DocumentRepository {

    override suspend fun createDocument(title: String, content: String, folderId: Long?): Long {
        val words = content.trim().split("\\s+".toRegex()).count { it.isNotBlank() }
        val doc = Document(
            title = title.ifBlank { "Untitled Document" },
            content = content,
            plainText = content,
            folderId = folderId,
            wordCount = words,
            characterCount = content.length
        )
        return database.insertDocument(doc)
    }

    override suspend fun updateDocument(document: Document) {
        val words = document.content.trim().split("\\s+".toRegex()).count { it.isNotBlank() }
        database.updateDocument(
            document.copy(
                wordCount = words,
                characterCount = document.content.length,
                plainText = document.content
            )
        )
    }

    override suspend fun getDocumentById(id: Long): Document? = database.getDocumentById(id)

    override suspend fun duplicateDocument(id: Long): Long? = database.duplicateDocument(id)

    override suspend fun deleteDocument(id: Long) = database.softDeleteDocument(id)

    override suspend fun restoreDocument(id: Long) = database.restoreDocument(id)

    override suspend fun permanentlyDeleteDocument(id: Long) = database.permanentlyDeleteDocument(id)

    override suspend fun emptyRecycleBin() = database.emptyRecycleBin()

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = database.setFavorite(id, isFavorite)

    override suspend fun toggleArchive(id: Long, isArchived: Boolean) = database.setArchived(id, isArchived)

    override suspend fun moveDocumentToFolder(id: Long, folderId: Long?) = database.moveToFolder(id, folderId)

    override suspend fun searchDocuments(query: String): List<Document> = database.searchDocuments(query)

    override fun observeAllDocuments(): Flow<List<Document>> =
        database.observeAllDocuments(filterDeleted = true, onlyFavorites = false, onlyArchived = false)

    override fun observeRecentDocuments(limit: Int): Flow<List<Document>> =
        database.observeRecentDocuments(limit)

    override fun observeFavorites(): Flow<List<Document>> =
        database.observeAllDocuments(filterDeleted = true, onlyFavorites = true)

    override fun observeArchived(): Flow<List<Document>> =
        database.observeAllDocuments(filterDeleted = true, onlyArchived = true)

    override fun observeRecycleBin(): Flow<List<Document>> =
        database.observeAllDocuments(filterDeleted = false)

    override fun observeFolderDocuments(folderId: Long): Flow<List<Document>> =
        database.observeAllDocuments(filterDeleted = true, folderId = folderId)

    override suspend fun createFolder(name: String, colorHex: String): Long =
        database.insertFolder(name, colorHex)

    override suspend fun updateFolder(id: Long, name: String, colorHex: String) =
        database.updateFolder(id, name, colorHex)

    override suspend fun deleteFolder(id: Long) = database.deleteFolder(id)

    override fun observeFolders(): Flow<List<Folder>> = database.observeFolders()

    override suspend fun recordSearch(query: String) = database.recordRecentSearch(query)

    override suspend fun getRecentSearches(): List<String> = database.getRecentSearches()

    override suspend fun deleteSearch(query: String) {
        database.deleteRecentSearch(query)
    }

    override suspend fun clearSearches() {
        database.clearRecentSearches()
    }
}
