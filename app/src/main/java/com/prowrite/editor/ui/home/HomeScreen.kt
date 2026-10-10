package com.prowrite.editor.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prowrite.editor.core.datastore.DocumentLayoutMode
import com.prowrite.editor.core.datastore.DocumentSortOption
import com.prowrite.editor.core.model.Document
import com.prowrite.editor.core.model.TemplateRegistry
import com.prowrite.editor.core.util.ExportUtils
import com.prowrite.editor.ui.components.DeleteConfirmationDialog
import com.prowrite.editor.ui.components.DocumentDetailsBottomSheet
import com.prowrite.editor.ui.components.DocumentGridCard
import com.prowrite.editor.ui.components.DocumentListItem
import com.prowrite.editor.ui.components.EmptyState
import com.prowrite.editor.ui.components.MoveToFolderDialog
import com.prowrite.editor.ui.components.RenameDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToEditor: (Long) -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToFolders: () -> Unit,
    onNavigateToRecycleBin: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog & Sheet States
    var showNewDocSheet by remember { mutableStateOf(false) }
    var renameDocTarget by remember { mutableStateOf<Document?>(null) }
    var deleteDocTarget by remember { mutableStateOf<Document?>(null) }
    var moveDocTarget by remember { mutableStateOf<Document?>(null) }
    var detailsDocTarget by remember { mutableStateOf<Document?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }

    val detailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        topBar = {
            if (state.isSelectionMode) {
                TopAppBar(
                    title = { Text("${state.selectedDocumentIds.size} Selected") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectAll() }) {
                            Icon(imageVector = Icons.Default.SelectAll, contentDescription = "Select all")
                        }
                        IconButton(onClick = { viewModel.deleteSelectedDocuments() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete selected",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            } else {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.EditNote,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "ProWrite",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = onNavigateToSearch,
                            modifier = Modifier.testTag("home_search_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search documents")
                        }
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier.testTag("home_settings_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewDocSheet = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Document") },
                modifier = Modifier.testTag("new_doc_fab")
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Quick navigation row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    QuickActionChip(
                        icon = Icons.Default.Add,
                        label = "Blank",
                        onClick = { viewModel.createBlankDocument(onNavigateToEditor) }
                    )
                }
                item {
                    QuickActionChip(
                        icon = Icons.Default.AutoAwesome,
                        label = "Templates",
                        onClick = onNavigateToTemplates
                    )
                }
                item {
                    QuickActionChip(
                        icon = Icons.Default.Folder,
                        label = "Folders",
                        onClick = onNavigateToFolders
                    )
                }
                item {
                    QuickActionChip(
                        icon = Icons.Default.Delete,
                        label = "Recycle Bin",
                        onClick = onNavigateToRecycleBin
                    )
                }
            }

            // Tabs: All, Recent, Favorites
            TabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = state.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = { Text("All Documents") }
                )
                Tab(
                    selected = state.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("Recent") }
                )
                Tab(
                    selected = state.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    text = { Text("Favorites") }
                )
            }

            // Filter & Control Bar (Layout toggle, Sort dropdown)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${state.documents.size} ${if (state.documents.size == 1) "document" else "documents"}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(imageVector = Icons.Default.Sort, contentDescription = "Sort options")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DocumentSortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.displayName) },
                                    onClick = {
                                        viewModel.setSortOption(option)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }

                    IconButton(onClick = { viewModel.toggleLayoutMode() }) {
                        Icon(
                            imageVector = if (state.layoutMode == DocumentLayoutMode.LIST) Icons.Default.GridView else Icons.Default.ViewList,
                            contentDescription = "Toggle view mode"
                        )
                    }
                }
            }

            // Document Content List/Grid
            if (state.documents.isEmpty()) {
                EmptyState(
                    title = when (state.selectedTab) {
                        1 -> "No Recent Documents"
                        2 -> "No Favorites Yet"
                        else -> "No Documents Yet"
                    },
                    description = when (state.selectedTab) {
                        1 -> "Documents you open will appear here for fast access."
                        2 -> "Star documents to keep them pinned here."
                        else -> "Create your first document to start writing."
                    },
                    actionLabel = "Create Document",
                    onActionClick = { viewModel.createBlankDocument(onNavigateToEditor) },
                    modifier = Modifier.weight(1f)
                )
            } else {
                if (state.layoutMode == DocumentLayoutMode.LIST) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.documents, key = { it.id }) { doc ->
                            DocumentListItem(
                                document = doc,
                                isSelected = state.selectedDocumentIds.contains(doc.id),
                                isSelectionMode = state.isSelectionMode,
                                onClick = {
                                    if (state.isSelectionMode) {
                                        viewModel.toggleSelection(doc.id)
                                    } else {
                                        onNavigateToEditor(doc.id)
                                    }
                                },
                                onLongClick = {
                                    viewModel.toggleSelection(doc.id)
                                },
                                onToggleFavorite = { viewModel.toggleFavorite(doc) },
                                onRename = { renameDocTarget = doc },
                                onDuplicate = { viewModel.duplicateDocument(doc.id) },
                                onMoveToFolder = { moveDocTarget = doc },
                                onArchive = { viewModel.toggleArchive(doc) },
                                onDelete = { deleteDocTarget = doc },
                                onDetails = { detailsDocTarget = doc }
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.documents, key = { it.id }) { doc ->
                            DocumentGridCard(
                                document = doc,
                                isSelected = state.selectedDocumentIds.contains(doc.id),
                                isSelectionMode = state.isSelectionMode,
                                onClick = {
                                    if (state.isSelectionMode) {
                                        viewModel.toggleSelection(doc.id)
                                    } else {
                                        onNavigateToEditor(doc.id)
                                    }
                                },
                                onLongClick = {
                                    viewModel.toggleSelection(doc.id)
                                },
                                onToggleFavorite = { viewModel.toggleFavorite(doc) },
                                onRename = { renameDocTarget = doc },
                                onDuplicate = { viewModel.duplicateDocument(doc.id) },
                                onMoveToFolder = { moveDocTarget = doc },
                                onArchive = { viewModel.toggleArchive(doc) },
                                onDelete = { deleteDocTarget = doc },
                                onDetails = { detailsDocTarget = doc }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet for New Document Creation
    if (showNewDocSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNewDocSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Create New Document",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showNewDocSheet = false
                            viewModel.createBlankDocument(onNavigateToEditor)
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Blank Document",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Start writing with a clean, empty canvas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Popular Templates",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                TemplateRegistry.templates.drop(1).take(3).forEach { template ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                showNewDocSheet = false
                                viewModel.createFromTemplate(template, onNavigateToEditor)
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = template.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                                )
                                Text(
                                    text = template.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showNewDocSheet = false
                            onNavigateToTemplates()
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Browse All Templates →",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    // Rename Dialog
    renameDocTarget?.let { doc ->
        RenameDialog(
            initialTitle = doc.title,
            onConfirm = { newTitle ->
                viewModel.renameDocument(doc.id, newTitle)
                renameDocTarget = null
            },
            onDismiss = { renameDocTarget = null }
        )
    }

    // Delete Dialog
    deleteDocTarget?.let { doc ->
        DeleteConfirmationDialog(
            title = "Move to Recycle Bin?",
            message = "You can restore \"${doc.title}\" at any time from the Recycle Bin.",
            onConfirm = {
                viewModel.deleteDocument(doc.id)
                deleteDocTarget = null
            },
            onDismiss = { deleteDocTarget = null }
        )
    }

    // Move to Folder Dialog
    moveDocTarget?.let { doc ->
        MoveToFolderDialog(
            folders = state.folders,
            currentFolderId = doc.folderId,
            onSelectFolder = { targetFolderId ->
                viewModel.moveDocument(doc.id, targetFolderId)
                moveDocTarget = null
            },
            onDismiss = { moveDocTarget = null }
        )
    }

    // Details & Export Sheet
    detailsDocTarget?.let { doc ->
        DocumentDetailsBottomSheet(
            document = doc,
            sheetState = detailsSheetState,
            onDismiss = { detailsDocTarget = null },
            onExportPdf = { ExportUtils.exportToPdf(context, doc) },
            onExportHtml = { ExportUtils.exportToHtml(context, doc) },
            onExportTxt = { ExportUtils.exportToTxt(context, doc) },
            onPrint = { ExportUtils.printDocument(context, doc) },
            onShare = { ExportUtils.shareDocument(context, doc) }
        )
    }
}

@Composable
private fun QuickActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    FilterChip(
        selected = false,
        onClick = onClick,
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
        },
        label = { Text(label) }
    )
}
