package com.prowrite.editor.core.di

import android.content.Context
import com.prowrite.editor.core.database.ProWriteDatabase
import com.prowrite.editor.core.datastore.PreferencesManager
import com.prowrite.editor.domain.repository.DocumentRepository
import com.prowrite.editor.domain.repository.DocumentRepositoryImpl

interface AppContainer {
    val documentRepository: DocumentRepository
    val preferencesManager: PreferencesManager
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database: ProWriteDatabase by lazy {
        ProWriteDatabase(context)
    }

    override val documentRepository: DocumentRepository by lazy {
        DocumentRepositoryImpl(database)
    }

    override val preferencesManager: PreferencesManager by lazy {
        PreferencesManager(context)
    }
}
