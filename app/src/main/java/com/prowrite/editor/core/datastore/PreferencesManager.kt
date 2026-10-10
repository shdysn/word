package com.prowrite.editor.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "prowrite_settings")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class DocumentLayoutMode {
    LIST,
    GRID
}

enum class DocumentSortOption(val displayName: String) {
    MODIFIED_DESC("Last modified"),
    MODIFIED_ASC("Oldest modified"),
    TITLE_ASC("Title (A-Z)"),
    TITLE_DESC("Title (Z-A)"),
    WORD_COUNT_DESC("Word count")
}

data class AppPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColor: Boolean = true,
    val layoutMode: DocumentLayoutMode = DocumentLayoutMode.LIST,
    val sortOption: DocumentSortOption = DocumentSortOption.MODIFIED_DESC,
    val defaultFontSize: Float = 16f,
    val autoSaveIntervalSec: Int = 3,
    val showWordCount: Boolean = true,
    val keepScreenAwake: Boolean = false,
    val onboardingCompleted: Boolean = true
)

class PreferencesManager(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val LAYOUT_MODE = stringPreferencesKey("layout_mode")
        val SORT_OPTION = stringPreferencesKey("sort_option")
        val DEFAULT_FONT_SIZE = floatPreferencesKey("default_font_size")
        val AUTO_SAVE_INTERVAL = intPreferencesKey("auto_save_interval")
        val SHOW_WORD_COUNT = booleanPreferencesKey("show_word_count")
        val KEEP_SCREEN_AWAKE = booleanPreferencesKey("keep_screen_awake")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val preferencesFlow: Flow<AppPreferences> = context.dataStore.data.map { prefs ->
        val themeStr = prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        val layoutStr = prefs[Keys.LAYOUT_MODE] ?: DocumentLayoutMode.LIST.name
        val sortStr = prefs[Keys.SORT_OPTION] ?: DocumentSortOption.MODIFIED_DESC.name

        AppPreferences(
            themeMode = runCatching { ThemeMode.valueOf(themeStr) }.getOrDefault(ThemeMode.SYSTEM),
            useDynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: true,
            layoutMode = runCatching { DocumentLayoutMode.valueOf(layoutStr) }.getOrDefault(DocumentLayoutMode.LIST),
            sortOption = runCatching { DocumentSortOption.valueOf(sortStr) }.getOrDefault(DocumentSortOption.MODIFIED_DESC),
            defaultFontSize = prefs[Keys.DEFAULT_FONT_SIZE] ?: 16f,
            autoSaveIntervalSec = prefs[Keys.AUTO_SAVE_INTERVAL] ?: 3,
            showWordCount = prefs[Keys.SHOW_WORD_COUNT] ?: true,
            keepScreenAwake = prefs[Keys.KEEP_SCREEN_AWAKE] ?: false,
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: true
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    suspend fun setLayoutMode(mode: DocumentLayoutMode) {
        context.dataStore.edit { it[Keys.LAYOUT_MODE] = mode.name }
    }

    suspend fun setSortOption(option: DocumentSortOption) {
        context.dataStore.edit { it[Keys.SORT_OPTION] = option.name }
    }

    suspend fun setDefaultFontSize(size: Float) {
        context.dataStore.edit { it[Keys.DEFAULT_FONT_SIZE] = size }
    }

    suspend fun setAutoSaveInterval(seconds: Int) {
        context.dataStore.edit { it[Keys.AUTO_SAVE_INTERVAL] = seconds }
    }

    suspend fun setShowWordCount(show: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_WORD_COUNT] = show }
    }

    suspend fun setKeepScreenAwake(keep: Boolean) {
        context.dataStore.edit { it[Keys.KEEP_SCREEN_AWAKE] = keep }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }
}
