package com.prowrite.editor.core.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.prowrite.editor.core.model.Document
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream

object ExportUtils {

    fun exportToPdf(context: Context, document: Document): File {
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // A4 standard pt width
        val pageHeight = 842 // A4 standard pt height
        val margin = 50f
        val contentWidth = pageWidth - (margin * 2)

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 12f
            isAntiAlias = true
        }

        val metaPaint = Paint().apply {
            color = Color.GRAY
            textSize = 10f
            isAntiAlias = true
        }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        var y = margin + 30f
        canvas.drawText(document.title, margin, y, titlePaint)
        y += 20f

        val metaText = "Words: ${document.wordCount} | Created with ProWrite"
        canvas.drawText(metaText, margin, y, metaPaint)
        y += 30f

        // Draw line separator
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint)
        y += 24f

        val lines = document.content.ifBlank { document.plainText }.lines()
        for (rawLine in lines) {
            val line = rawLine.trimEnd()
            if (line.isBlank()) {
                y += 16f
            } else {
                // Check page height limit
                if (y > pageHeight - margin - 40f) {
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    y = margin + 20f
                }

                // Simple bolding for headings
                if (line.startsWith("# ")) {
                    val h1Paint = Paint(titlePaint).apply { textSize = 16f }
                    canvas.drawText(line.removePrefix("# "), margin, y, h1Paint)
                    y += 24f
                } else if (line.startsWith("## ")) {
                    val h2Paint = Paint(titlePaint).apply { textSize = 14f }
                    canvas.drawText(line.removePrefix("## "), margin, y, h2Paint)
                    y += 20f
                } else {
                    // Wrap long text if needed
                    var remaining = line
                    while (remaining.isNotEmpty()) {
                        val count = bodyPaint.breakText(remaining, true, contentWidth, null)
                        val substring = remaining.substring(0, count)
                        canvas.drawText(substring, margin, y, bodyPaint)
                        y += 18f
                        remaining = remaining.substring(count).trimStart()

                        if (y > pageHeight - margin - 40f && remaining.isNotEmpty()) {
                            pdfDocument.finishPage(page)
                            pageNumber++
                            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                            page = pdfDocument.startPage(pageInfo)
                            canvas = page.canvas
                            y = margin + 20f
                        }
                    }
                }
            }
        }

        pdfDocument.finishPage(page)

        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val sanitizedTitle = document.title.replace("[^a-zA-Z0-9.-]".toRegex(), "_")
        val file = File(exportDir, "${sanitizedTitle}_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        return file
    }

    fun exportToHtml(context: Context, document: Document): File {
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <title>${document.title}</title>
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; line-height: 1.6; max-width: 800px; margin: 40px auto; padding: 0 20px; color: #1e293b; }
                    h1 { color: #1e3a8a; border-bottom: 2px solid #e2e8f0; padding-bottom: 8px; }
                    h2 { color: #2563eb; margin-top: 24px; }
                    .meta { color: #64748b; font-size: 0.9em; margin-bottom: 30px; }
                    p { margin-bottom: 16px; }
                </style>
            </head>
            <body>
                <h1>${document.title}</h1>
                <div class="meta">Word count: ${document.wordCount} | Generated by ProWrite</div>
                <div>
                    ${document.content.replace("\n\n", "<p></p>").replace("\n", "<br>")}
                </div>
            </body>
            </html>
        """.trimIndent()

        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val sanitizedTitle = document.title.replace("[^a-zA-Z0-9.-]".toRegex(), "_")
        val file = File(exportDir, "${sanitizedTitle}_${System.currentTimeMillis()}.html")
        file.writeText(htmlContent)
        return file
    }

    fun exportToTxt(context: Context, document: Document): File {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val sanitizedTitle = document.title.replace("[^a-zA-Z0-9.-]".toRegex(), "_")
        val file = File(exportDir, "${sanitizedTitle}_${System.currentTimeMillis()}.txt")
        file.writeText(document.content.ifBlank { document.plainText })
        return file
    }

    fun shareDocument(context: Context, document: Document) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, document.title)
            putExtra(Intent.EXTRA_TEXT, "${document.title}\n\n${document.content.ifBlank { document.plainText }}")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Document")
        context.startActivity(shareIntent)
    }

    fun printDocument(context: Context, document: Document) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "${document.title} - ProWrite Print"

        printManager.print(jobName, object : PrintDocumentAdapter() {
            private var tempPdfFile: File? = null

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }

                tempPdfFile = exportToPdf(context, document)
                val info = PrintDocumentInfo.Builder("${document.title}.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build()
                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                val file = tempPdfFile ?: return
                try {
                    FileInputStream(file).use { input ->
                        FileOutputStream(destination?.fileDescriptor).use { output ->
                            input.copyTo(output)
                        }
                    }
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                }
            }
        }, null)
    }
}
