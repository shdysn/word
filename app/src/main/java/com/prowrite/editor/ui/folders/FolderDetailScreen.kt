package com.prowrite.editor.ui.folders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prowrite.editor.domain.repository.DocumentRepository
import com.prowrite.editor.ui.components.DocumentListItem
import com.prowrite.editor.ui.components.EmptyState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailScreen(
    folderId: Long,
    folderName: String,
    repository: DocumentRepository,
    onNavigateToEditor: (Long) -> Unit,
    onBack: () -> Unit
) {
    val documents by repository.observeFolderDocuments(folderId).collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(folderName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        val id = repository.createDocument("Untitled Document", "", folderId)
                        onNavigateToEditor(id)
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Document to Folder")
            }
        }
    ) { padding ->
        if (documents.isEmpty()) {
            EmptyState(
                title = "Folder is Empty",
                description = "Create a new document inside this folder or move existing documents here.",
                actionLabel = "Create Document Here",
                onActionClick = {
                    scope.launch {
                        val id = repository.createDocument("Untitled Document", "", folderId)
                        onNavigateToEditor(id)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(documents, key = { it.id }) { doc ->
                    DocumentListItem(
                        document = doc,
                        isSelected = false,
                        isSelectionMode = false,
                        onClick = { onNavigateToEditor(doc.id) },
                        onLongClick = {},
                        onToggleFavorite = {
                            scope.launch { repository.toggleFavorite(doc.id, !doc.isFavorite) }
                        },
                        onRename = {},
                        onDuplicate = {
                            scope.launch { repository.duplicateDocument(doc.id) }
                        },
                        onMoveToFolder = {
                            scope.launch { repository.moveDocumentToFolder(doc.id, null) }
                        },
                        onArchive = {
                            scope.launch { repository.toggleArchive(doc.id, !doc.isArchived) }
                        },
                        onDelete = {
                            scope.launch { repository.deleteDocument(doc.id) }
                        },
                        onDetails = {}
                    )
                }
            }
        }
    }
}
