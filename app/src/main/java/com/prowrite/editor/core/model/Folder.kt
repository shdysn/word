package com.prowrite.editor.core.model

data class Folder(
    val id: Long = 0,
    val name: String,
    val colorHex: String = "#1E3A8A",
    val createdAt: Long = System.currentTimeMillis(),
    val documentCount: Int = 0
)
