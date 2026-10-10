package com.prowrite.editor.core.model

data class Document(
    val id: Long = 0,
    val title: String,
    val content: String = "",
    val plainText: String = "",
    val folderId: Long? = null,
    val folderName: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastOpenedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val wordCount: Int = 0,
    val characterCount: Int = 0,
    val fileSize: Long = 0
) {
    val previewText: String
        get() = if (plainText.isNotBlank()) {
            plainText.lines().filter { it.isNotBlank() }.take(2).joinToString(" ")
        } else if (content.isNotBlank()) {
            content.lines().filter { it.isNotBlank() }.take(2).joinToString(" ")
        } else {
            "No content"
        }
}
