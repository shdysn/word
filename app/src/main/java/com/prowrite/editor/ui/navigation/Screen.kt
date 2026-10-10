package com.prowrite.editor.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Editor : Screen("editor/{documentId}") {
        fun createRoute(documentId: Long): String = "editor/$documentId"
    }
    data object Templates : Screen("templates")
    data object Folders : Screen("folders")
    data object FolderDetail : Screen("folders/{folderId}/{folderName}") {
        fun createRoute(folderId: Long, folderName: String): String = "folders/$folderId/$folderName"
    }
    data object RecycleBin : Screen("recycle_bin")
    data object Search : Screen("search")
    data object Settings : Screen("settings")
}
