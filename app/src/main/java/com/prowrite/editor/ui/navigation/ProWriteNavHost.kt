package com.prowrite.editor.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.prowrite.editor.core.di.AppContainer
import com.prowrite.editor.ui.editor.EditorScreen
import com.prowrite.editor.ui.editor.EditorViewModel
import com.prowrite.editor.ui.folders.FolderDetailScreen
import com.prowrite.editor.ui.folders.FoldersScreen
import com.prowrite.editor.ui.home.HomeScreen
import com.prowrite.editor.ui.home.HomeViewModel
import com.prowrite.editor.ui.recyclebin.RecycleBinScreen
import com.prowrite.editor.ui.search.SearchScreen
import com.prowrite.editor.ui.settings.SettingsScreen
import com.prowrite.editor.ui.templates.TemplatesScreen

@Composable
fun ProWriteNavHost(
    navController: NavHostController,
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(
                    container.documentRepository,
                    container.preferencesManager
                )
            )
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToEditor = { docId ->
                    navController.navigate(Screen.Editor.createRoute(docId))
                },
                onNavigateToTemplates = {
                    navController.navigate(Screen.Templates.route)
                },
                onNavigateToFolders = {
                    navController.navigate(Screen.Folders.route)
                },
                onNavigateToRecycleBin = {
                    navController.navigate(Screen.RecycleBin.route)
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Editor.route,
            arguments = listOf(navArgument("documentId") { type = NavType.LongType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getLong("documentId") ?: 0L
            val editorViewModel: EditorViewModel = viewModel(
                key = "editor_$documentId",
                factory = EditorViewModel.provideFactory(
                    documentId,
                    container.documentRepository,
                    container.preferencesManager
                )
            )
            EditorScreen(
                viewModel = editorViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Templates.route) {
            TemplatesScreen(
                repository = container.documentRepository,
                onNavigateToEditor = { docId ->
                    navController.navigate(Screen.Editor.createRoute(docId)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Folders.route) {
            FoldersScreen(
                repository = container.documentRepository,
                onNavigateToFolderDetail = { folderId, folderName ->
                    navController.navigate(Screen.FolderDetail.createRoute(folderId, folderName))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.FolderDetail.route,
            arguments = listOf(
                navArgument("folderId") { type = NavType.LongType },
                navArgument("folderName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val folderId = backStackEntry.arguments?.getLong("folderId") ?: 0L
            val folderName = backStackEntry.arguments?.getString("folderName") ?: "Folder"
            FolderDetailScreen(
                folderId = folderId,
                folderName = folderName,
                repository = container.documentRepository,
                onNavigateToEditor = { docId ->
                    navController.navigate(Screen.Editor.createRoute(docId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RecycleBin.route) {
            RecycleBinScreen(
                repository = container.documentRepository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                repository = container.documentRepository,
                onNavigateToEditor = { docId ->
                    navController.navigate(Screen.Editor.createRoute(docId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                preferencesManager = container.preferencesManager,
                repository = container.documentRepository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
