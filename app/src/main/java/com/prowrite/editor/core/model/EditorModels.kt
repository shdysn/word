package com.prowrite.editor.core.model

enum class HeadingLevel(val label: String, val fontSizeSp: Float, val isBold: Boolean) {
    NORMAL("Normal Text", 15f, false),
    TITLE("Document Title", 26f, true),
    HEADING_1("Heading 1", 22f, true),
    HEADING_2("Heading 2", 18f, true),
    HEADING_3("Heading 3", 16f, true)
}

enum class TextAlignment(val label: String) {
    LEFT("Align Left"),
    CENTER("Align Center"),
    RIGHT("Align Right")
}

data class FormattingState(
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val isStrikethrough: Boolean = false,
    val headingLevel: HeadingLevel = HeadingLevel.NORMAL,
    val alignment: TextAlignment = TextAlignment.LEFT,
    val fontSizeSp: Float = 16f,
    val textColorHex: String = "#000000",
    val highlightColorHex: String? = null,
    val isBulletList: Boolean = false,
    val isNumberedList: Boolean = false
)

enum class SaveStatus {
    SAVED,
    SAVING,
    UNSAVED_CHANGES,
    ERROR
}

data class FindReplaceState(
    val isActive: Boolean = false,
    val findQuery: String = "",
    val replaceQuery: String = "",
    val matchIndices: List<Int> = emptyList(),
    val currentMatchIndex: Int = -1,
    val isCaseSensitive: Boolean = false
)
