package com.prowrite.editor

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.prowrite.editor.core.datastore.AppPreferences
import com.prowrite.editor.core.designsystem.ProWriteTheme
import com.prowrite.editor.ui.navigation.ProWriteNavHost

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as ProWriteApplication
        val container = app.container

        setContent {
            val preferences by container.preferencesManager.preferencesFlow
                .collectAsStateWithLifecycle(initialValue = AppPreferences())

            // Support Keep Screen Awake preference
            LaunchedEffect(preferences.keepScreenAwake) {
                if (preferences.keepScreenAwake) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            ProWriteTheme(
                themeMode = preferences.themeMode,
                dynamicColor = preferences.useDynamicColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    ProWriteNavHost(
                        navController = navController,
                        container = container
                    )
                }
            }
        }
    }
}
