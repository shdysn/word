package com.prowrite.editor

import com.prowrite.editor.core.model.Document
import com.prowrite.editor.core.model.HeadingLevel
import com.prowrite.editor.core.model.TemplateRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DocumentModelTest {

    @Test
    fun testDocumentPreviewTextGeneration() {
        val doc = Document(
            title = "Test Doc",
            content = "First line of text.\nSecond line of text.\nThird line of text."
        )
        assertTrue(doc.previewText.contains("First line of text."))
        assertTrue(doc.previewText.contains("Second line of text."))
        assertFalse(doc.previewText.contains("Third line of text."))
    }

    @Test
    fun testEmptyDocumentPreview() {
        val doc = Document(
            title = "Empty Doc",
            content = ""
        )
        assertEquals("No content", doc.previewText)
    }

    @Test
    fun testTemplateRegistryContainsCoreTemplates() {
        val templates = TemplateRegistry.templates
        assertTrue(templates.any { it.id == "blank" })
        assertTrue(templates.any { it.id == "resume" })
        assertTrue(templates.any { it.id == "formal_letter" })
        assertTrue(templates.any { it.id == "assignment" })
        assertTrue(templates.any { it.id == "research_notes" })
        assertTrue(templates.any { it.id == "meeting_notes" })
        assertTrue(templates.any { it.id == "project_report" })
    }

    @Test
    fun testHeadingLevelDefinitions() {
        assertEquals("Document Title", HeadingLevel.TITLE.label)
        assertTrue(HeadingLevel.TITLE.isBold)
        assertFalse(HeadingLevel.NORMAL.isBold)
    }
}
