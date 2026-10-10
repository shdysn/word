package com.prowrite.editor

import android.app.Application
import com.prowrite.editor.core.di.AppContainer
import com.prowrite.editor.core.di.DefaultAppContainer

class ProWriteApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
