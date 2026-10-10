package com.prowrite.editor.domain.usecase

import com.prowrite.editor.core.model.Document
import com.prowrite.editor.core.model.Folder
import com.prowrite.editor.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow

class CreateDocumentUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(title: String, content: String = "", folderId: Long? = null): Long {
        return repository.createDocument(title, content, folderId)
    }
}

class UpdateDocumentUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(document: Document) {
        repository.updateDocument(document)
    }
}

class GetDocumentUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(id: Long): Document? = repository.getDocumentById(id)
}

class DeleteDocumentUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteDocument(id)
}

class RestoreDocumentUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(id: Long) = repository.restoreDocument(id)
}

class PermanentlyDeleteDocumentUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(id: Long) = repository.permanentlyDeleteDocument(id)
}

class DuplicateDocumentUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(id: Long): Long? = repository.duplicateDocument(id)
}

class ToggleFavoriteUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(id: Long, isFavorite: Boolean) = repository.toggleFavorite(id, isFavorite)
}

class ToggleArchiveUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(id: Long, isArchived: Boolean) = repository.toggleArchive(id, isArchived)
}

class MoveDocumentUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(id: Long, folderId: Long?) = repository.moveDocumentToFolder(id, folderId)
}

class SearchDocumentsUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(query: String): List<Document> = repository.searchDocuments(query)
}
