package com.prowrite.editor.core.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.prowrite.editor.core.model.Document
import com.prowrite.editor.core.model.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ProWriteDatabase(context: Context) {

    private val dbHelper = DatabaseHelper(context.applicationContext)
    private val changeNotifier = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private fun notifyChanged() {
        changeNotifier.tryEmit(Unit)
    }

    private class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        companion object {
            const val DATABASE_NAME = "prowrite.db"
            const val DATABASE_VERSION = 1

            const val TABLE_DOCUMENTS = "documents"
            const val TABLE_FOLDERS = "folders"
            const val TABLE_RECENT_SEARCHES = "recent_searches"
        }

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE $TABLE_FOLDERS (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    color_hex TEXT NOT NULL,
                    created_at INTEGER NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE $TABLE_DOCUMENTS (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    content TEXT NOT NULL,
                    plain_text TEXT NOT NULL,
                    folder_id INTEGER,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL,
                    last_opened_at INTEGER NOT NULL,
                    deleted_at INTEGER,
                    is_favorite INTEGER NOT NULL DEFAULT 0,
                    is_archived INTEGER NOT NULL DEFAULT 0,
                    is_deleted INTEGER NOT NULL DEFAULT 0,
                    word_count INTEGER NOT NULL DEFAULT 0,
                    character_count INTEGER NOT NULL DEFAULT 0,
                    file_size INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY(folder_id) REFERENCES $TABLE_FOLDERS(id) ON DELETE SET NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE $TABLE_RECENT_SEARCHES (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    query TEXT UNIQUE NOT NULL,
                    searched_at INTEGER NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL("CREATE INDEX idx_docs_deleted ON $TABLE_DOCUMENTS(is_deleted)")
            db.execSQL("CREATE INDEX idx_docs_favorite ON $TABLE_DOCUMENTS(is_favorite)")
            db.execSQL("CREATE INDEX idx_docs_updated ON $TABLE_DOCUMENTS(updated_at)")
            db.execSQL("CREATE INDEX idx_docs_folder ON $TABLE_DOCUMENTS(folder_id)")

            // Seed initial sample documents so the user immediately has realistic documents to test and edit
            insertSampleDocuments(db)
        }

        private fun insertSampleDocuments(db: SQLiteDatabase) {
            val now = System.currentTimeMillis()

            val f1 = ContentValues().apply {
                put("name", "Work Projects")
                put("color_hex", "#1E3A8A")
                put("created_at", now - 86400000L * 5)
            }
            val folderId1 = db.insert(TABLE_FOLDERS, null, f1)

            val f2 = ContentValues().apply {
                put("name", "Personal Notes")
                put("color_hex", "#0D9488")
                put("created_at", now - 86400000L * 3)
            }
            db.insert(TABLE_FOLDERS, null, f2)

            val doc1 = ContentValues().apply {
                put("title", "Project Vision & Strategy 2026")
                put(
                    "content",
                    """
# Project Vision & Strategy 2026

## Executive Overview
ProWrite is engineered to deliver a world-class, offline-first mobile document editing platform. Our vision centers around zero latency, elegant Material 3 design, and powerful rich formatting for writers and professionals.

## Core Pillars
1. **Uncompromised Offline Autonomy:** All documents stay local and private on device.
2. **Speed & Ergonomics:** Effortless text entry with instant formatting bar and smooth pagination.
3. **Multi-Format Export:** High-fidelity PDF rendering, HTML publication, and pure text exports.

## Next Steps
- Finalize user testing with university researchers and writers.
- Expand folder-level organization and batch operations.
                    """.trimIndent()
                )
                put("plain_text", "Project Vision & Strategy 2026. Executive Overview: ProWrite is engineered to deliver a world-class...")
                put("folder_id", folderId1)
                put("created_at", now - 86400000L * 2)
                put("updated_at", now - 3600000L * 2)
                put("last_opened_at", now - 3600000L * 2)
                put("is_favorite", 1)
                put("is_archived", 0)
                put("is_deleted", 0)
                put("word_count", 92)
                put("character_count", 640)
                put("file_size", 640)
            }
            db.insert(TABLE_DOCUMENTS, null, doc1)

            val doc2 = ContentValues().apply {
                put("title", "Meeting Notes: Sprint Review")
                put(
                    "content",
                    """
# Meeting Notes: Sprint Review

**Date:** Yesterday | 2:00 PM
**Attendees:** Team Leads & Product Managers

### Highlights
- Document editor layout validated on phones and tablets.
- Search with highlighted queries tested successfully.
- Color palette upgraded to royal indigo and teal accents.

### Decisions
- Retain undo and redo history for at least 50 steps.
- Provide direct print via Android Print Framework.
                    """.trimIndent()
                )
                put("plain_text", "Meeting Notes: Sprint Review. Date: Yesterday | 2:00 PM. Attendees: Team Leads & Product Managers...")
                put("folder_id", folderId1)
                put("created_at", now - 86400000L)
                put("updated_at", now - 3600000L * 8)
                put("last_opened_at", now - 3600000L * 8)
                put("is_favorite", 0)
                put("is_archived", 0)
                put("is_deleted", 0)
                put("word_count", 64)
                put("character_count", 412)
                put("file_size", 412)
            }
            db.insert(TABLE_DOCUMENTS, null, doc2)

            val doc3 = ContentValues().apply {
                put("title", "Weekly Writing Journal")
                put(
                    "content",
                    """
# Weekly Writing Journal

"Writing is the geometry of the soul."

Reflecting on productivity habits this week:
- Morning drafting sessions are consistently 40% more focused.
- Distraction-free editing canvas greatly improves flow state.
- Keeping screen awake while writing prevents abrupt context switches.
                    """.trimIndent()
                )
                put("plain_text", "Weekly Writing Journal. Writing is the geometry of the soul. Reflecting on productivity habits...")
                putNull("folder_id")
                put("created_at", now - 86400000L * 4)
                put("updated_at", now - 86400000L)
                put("last_opened_at", now - 86400000L)
                put("is_favorite", 1)
                put("is_archived", 0)
                put("is_deleted", 0)
                put("word_count", 48)
                put("character_count", 328)
                put("file_size", 328)
            }
            db.insert(TABLE_DOCUMENTS, null, doc3)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // Future migration support
        }
    }

    // --- Document Operations ---

    suspend fun insertDocument(document: Document): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", document.title)
            put("content", document.content)
            put("plain_text", document.plainText)
            if (document.folderId != null) put("folder_id", document.folderId) else putNull("folder_id")
            put("created_at", document.createdAt)
            put("updated_at", document.updatedAt)
            put("last_opened_at", document.lastOpenedAt)
            if (document.deletedAt != null) put("deleted_at", document.deletedAt) else putNull("deleted_at")
            put("is_favorite", if (document.isFavorite) 1 else 0)
            put("is_archived", if (document.isArchived) 1 else 0)
            put("is_deleted", if (document.isDeleted) 1 else 0)
            put("word_count", document.wordCount)
            put("character_count", document.characterCount)
            put("file_size", document.content.toByteArray().size.toLong())
        }
        val id = db.insert(DatabaseHelper.TABLE_DOCUMENTS, null, values)
        notifyChanged()
        id
    }

    suspend fun updateDocument(document: Document) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", document.title)
            put("content", document.content)
            put("plain_text", document.plainText)
            if (document.folderId != null) put("folder_id", document.folderId) else putNull("folder_id")
            put("updated_at", System.currentTimeMillis())
            put("last_opened_at", document.lastOpenedAt)
            if (document.deletedAt != null) put("deleted_at", document.deletedAt) else putNull("deleted_at")
            put("is_favorite", if (document.isFavorite) 1 else 0)
            put("is_archived", if (document.isArchived) 1 else 0)
            put("is_deleted", if (document.isDeleted) 1 else 0)
            put("word_count", document.wordCount)
            put("character_count", document.characterCount)
            put("file_size", document.content.toByteArray().size.toLong())
        }
        db.update(DatabaseHelper.TABLE_DOCUMENTS, values, "id = ?", arrayOf(document.id.toString()))
        notifyChanged()
    }

    suspend fun getDocumentById(id: Long): Document? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT d.*, f.name as folder_name 
            FROM ${DatabaseHelper.TABLE_DOCUMENTS} d 
            LEFT JOIN ${DatabaseHelper.TABLE_FOLDERS} f ON d.folder_id = f.id 
            WHERE d.id = ?
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        cursor.use {
            if (it.moveToFirst()) parseDocument(it) else null
        }
    }

    suspend fun getAllDocuments(
        filterDeleted: Boolean = true,
        onlyFavorites: Boolean = false,
        onlyArchived: Boolean = false,
        folderId: Long? = null
    ): List<Document> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val whereClauses = mutableListOf<String>()
        val args = mutableListOf<String>()

        if (filterDeleted) {
            whereClauses.add("d.is_deleted = 0")
        } else {
            whereClauses.add("d.is_deleted = 1")
        }

        if (onlyFavorites) {
            whereClauses.add("d.is_favorite = 1")
        }

        if (onlyArchived) {
            whereClauses.add("d.is_archived = 1")
        } else if (filterDeleted) {
            whereClauses.add("d.is_archived = 0")
        }

        if (folderId != null) {
            whereClauses.add("d.folder_id = ?")
            args.add(folderId.toString())
        }

        val whereSql = if (whereClauses.isNotEmpty()) "WHERE " + whereClauses.joinToString(" AND ") else ""
        val sql = """
            SELECT d.*, f.name as folder_name 
            FROM ${DatabaseHelper.TABLE_DOCUMENTS} d 
            LEFT JOIN ${DatabaseHelper.TABLE_FOLDERS} f ON d.folder_id = f.id 
            $whereSql
            ORDER BY d.updated_at DESC
        """.trimIndent()

        val cursor = db.rawQuery(sql, args.toTypedArray())
        val list = mutableListOf<Document>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(parseDocument(it))
            }
        }
        list
    }

    suspend fun getRecentDocuments(limit: Int = 10): List<Document> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT d.*, f.name as folder_name 
            FROM ${DatabaseHelper.TABLE_DOCUMENTS} d 
            LEFT JOIN ${DatabaseHelper.TABLE_FOLDERS} f ON d.folder_id = f.id 
            WHERE d.is_deleted = 0 AND d.is_archived = 0
            ORDER BY d.last_opened_at DESC
            LIMIT ?
        """.trimIndent()
        val cursor = db.rawQuery(sql, arrayOf(limit.toString()))
        val list = mutableListOf<Document>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(parseDocument(it))
            }
        }
        list
    }

    suspend fun searchDocuments(query: String): List<Document> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val db = dbHelper.readableDatabase
        val pattern = "%$query%"
        val sql = """
            SELECT d.*, f.name as folder_name 
            FROM ${DatabaseHelper.TABLE_DOCUMENTS} d 
            LEFT JOIN ${DatabaseHelper.TABLE_FOLDERS} f ON d.folder_id = f.id 
            WHERE d.is_deleted = 0 AND (d.title LIKE ? OR d.content LIKE ?)
            ORDER BY d.updated_at DESC
        """.trimIndent()
        val cursor = db.rawQuery(sql, arrayOf(pattern, pattern))
        val list = mutableListOf<Document>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(parseDocument(it))
            }
        }
        list
    }

    suspend fun setFavorite(id: Long, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("is_favorite", if (isFavorite) 1 else 0)
        }
        db.update(DatabaseHelper.TABLE_DOCUMENTS, values, "id = ?", arrayOf(id.toString()))
        notifyChanged()
    }

    suspend fun setArchived(id: Long, isArchived: Boolean) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("is_archived", if (isArchived) 1 else 0)
        }
        db.update(DatabaseHelper.TABLE_DOCUMENTS, values, "id = ?", arrayOf(id.toString()))
        notifyChanged()
    }

    suspend fun moveToFolder(id: Long, folderId: Long?) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            if (folderId != null) put("folder_id", folderId) else putNull("folder_id")
        }
        db.update(DatabaseHelper.TABLE_DOCUMENTS, values, "id = ?", arrayOf(id.toString()))
        notifyChanged()
    }

    suspend fun softDeleteDocument(id: Long) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("is_deleted", 1)
            put("deleted_at", System.currentTimeMillis())
        }
        db.update(DatabaseHelper.TABLE_DOCUMENTS, values, "id = ?", arrayOf(id.toString()))
        notifyChanged()
    }

    suspend fun restoreDocument(id: Long) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("is_deleted", 0)
            putNull("deleted_at")
        }
        db.update(DatabaseHelper.TABLE_DOCUMENTS, values, "id = ?", arrayOf(id.toString()))
        notifyChanged()
    }

    suspend fun permanentlyDeleteDocument(id: Long) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_DOCUMENTS, "id = ?", arrayOf(id.toString()))
        notifyChanged()
    }

    suspend fun emptyRecycleBin() = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_DOCUMENTS, "is_deleted = 1", null)
        notifyChanged()
    }

    suspend fun duplicateDocument(id: Long): Long? = withContext(Dispatchers.IO) {
        val doc = getDocumentById(id) ?: return@withContext null
        val copy = doc.copy(
            id = 0,
            title = "${doc.title} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            lastOpenedAt = System.currentTimeMillis()
        )
        insertDocument(copy)
    }

    // --- Folder Operations ---

    suspend fun insertFolder(name: String, colorHex: String): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("color_hex", colorHex)
            put("created_at", System.currentTimeMillis())
        }
        val id = db.insert(DatabaseHelper.TABLE_FOLDERS, null, values)
        notifyChanged()
        id
    }

    suspend fun updateFolder(id: Long, name: String, colorHex: String) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("color_hex", colorHex)
        }
        db.update(DatabaseHelper.TABLE_FOLDERS, values, "id = ?", arrayOf(id.toString()))
        notifyChanged()
    }

    suspend fun deleteFolder(id: Long) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        // Move docs in this folder to root (folder_id = null)
        val resetDocs = ContentValues().apply { putNull("folder_id") }
        db.update(DatabaseHelper.TABLE_DOCUMENTS, resetDocs, "folder_id = ?", arrayOf(id.toString()))
        db.delete(DatabaseHelper.TABLE_FOLDERS, "id = ?", arrayOf(id.toString()))
        notifyChanged()
    }

    suspend fun getAllFolders(): List<Folder> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT f.*, COUNT(d.id) as doc_count 
            FROM ${DatabaseHelper.TABLE_FOLDERS} f 
            LEFT JOIN ${DatabaseHelper.TABLE_DOCUMENTS} d ON f.id = d.folder_id AND d.is_deleted = 0 
            GROUP BY f.id 
            ORDER BY f.name ASC
        """.trimIndent()
        val cursor = db.rawQuery(sql, null)
        val list = mutableListOf<Folder>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Folder(
                        id = it.getLong(it.getColumnIndexOrThrow("id")),
                        name = it.getString(it.getColumnIndexOrThrow("name")),
                        colorHex = it.getString(it.getColumnIndexOrThrow("color_hex")),
                        createdAt = it.getLong(it.getColumnIndexOrThrow("created_at")),
                        documentCount = it.getInt(it.getColumnIndexOrThrow("doc_count"))
                    )
                )
            }
        }
        list
    }

    // --- Recent Searches ---

    suspend fun recordRecentSearch(query: String) = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("query", query.trim())
            put("searched_at", System.currentTimeMillis())
        }
        db.insertWithOnConflict(
            DatabaseHelper.TABLE_RECENT_SEARCHES,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    suspend fun getRecentSearches(limit: Int = 8): List<String> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_RECENT_SEARCHES,
            arrayOf("query"),
            null,
            null,
            null,
            null,
            "searched_at DESC",
            limit.toString()
        )
        val list = mutableListOf<String>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(it.getString(0))
            }
        }
        list
    }

    suspend fun deleteRecentSearch(query: String) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_RECENT_SEARCHES, "query = ?", arrayOf(query))
    }

    suspend fun clearRecentSearches() = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_RECENT_SEARCHES, null, null)
    }

    // --- Reactive Flow ---

    fun observeAllDocuments(
        filterDeleted: Boolean = true,
        onlyFavorites: Boolean = false,
        onlyArchived: Boolean = false,
        folderId: Long? = null
    ): Flow<List<Document>> = flow {
        emit(getAllDocuments(filterDeleted, onlyFavorites, onlyArchived, folderId))
        changeNotifier.collect {
            emit(getAllDocuments(filterDeleted, onlyFavorites, onlyArchived, folderId))
        }
    }.flowOn(Dispatchers.IO)

    fun observeRecentDocuments(limit: Int = 10): Flow<List<Document>> = flow {
        emit(getRecentDocuments(limit))
        changeNotifier.collect {
            emit(getRecentDocuments(limit))
        }
    }.flowOn(Dispatchers.IO)

    fun observeFolders(): Flow<List<Folder>> = flow {
        emit(getAllFolders())
        changeNotifier.collect {
            emit(getAllFolders())
        }
    }.flowOn(Dispatchers.IO)

    private fun parseDocument(c: Cursor): Document {
        val folderIndex = c.getColumnIndex("folder_name")
        val folderName = if (folderIndex >= 0 && !c.isNull(folderIndex)) c.getString(folderIndex) else null
        val content = c.getString(c.getColumnIndexOrThrow("content"))
        val plainText = c.getString(c.getColumnIndexOrThrow("plain_text"))

        return Document(
            id = c.getLong(c.getColumnIndexOrThrow("id")),
            title = c.getString(c.getColumnIndexOrThrow("title")),
            content = content,
            plainText = if (plainText.isBlank()) content else plainText,
            folderId = if (c.isNull(c.getColumnIndexOrThrow("folder_id"))) null else c.getLong(c.getColumnIndexOrThrow("folder_id")),
            folderName = folderName,
            createdAt = c.getLong(c.getColumnIndexOrThrow("created_at")),
            updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at")),
            lastOpenedAt = c.getLong(c.getColumnIndexOrThrow("last_opened_at")),
            deletedAt = if (c.isNull(c.getColumnIndexOrThrow("deleted_at"))) null else c.getLong(c.getColumnIndexOrThrow("deleted_at")),
            isFavorite = c.getInt(c.getColumnIndexOrThrow("is_favorite")) == 1,
            isArchived = c.getInt(c.getColumnIndexOrThrow("is_archived")) == 1,
            isDeleted = c.getInt(c.getColumnIndexOrThrow("is_deleted")) == 1,
            wordCount = c.getInt(c.getColumnIndexOrThrow("word_count")),
            characterCount = c.getInt(c.getColumnIndexOrThrow("character_count")),
            fileSize = c.getLong(c.getColumnIndexOrThrow("file_size"))
        )
    }
}
