package com.prowrite.editor.ui.recyclebin

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prowrite.editor.core.model.Document
import com.prowrite.editor.domain.repository.DocumentRepository
import com.prowrite.editor.ui.components.DeleteConfirmationDialog
import com.prowrite.editor.ui.components.EmptyState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinScreen(
    repository: DocumentRepository,
    onBack: () -> Unit
) {
    val deletedDocs by repository.observeRecycleBin().collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()

    var showEmptyBinDialog by remember { mutableStateOf(false) }
    var permanentDeleteTarget by remember { mutableStateOf<Document?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recycle Bin") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (deletedDocs.isNotEmpty()) {
                        IconButton(onClick = { showEmptyBinDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = "Empty Recycle Bin",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (deletedDocs.isEmpty()) {
            EmptyState(
                title = "Recycle Bin is Empty",
                description = "Deleted documents are retained here and can be restored at any time.",
                icon = Icons.Default.Delete,
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
                items(deletedDocs, key = { it.id }) { doc ->
                    DeletedDocumentItem(
                        document = doc,
                        onRestore = {
                            scope.launch { repository.restoreDocument(doc.id) }
                        },
                        onDeletePermanent = {
                            permanentDeleteTarget = doc
                        }
                    )
                }
            }
        }
    }

    if (showEmptyBinDialog) {
        DeleteConfirmationDialog(
            title = "Empty Recycle Bin?",
            message = "All documents in the recycle bin will be permanently removed. This action cannot be undone.",
            isPermanent = true,
            onConfirm = {
                scope.launch {
                    repository.emptyRecycleBin()
                    showEmptyBinDialog = false
                }
            },
            onDismiss = { showEmptyBinDialog = false }
        )
    }

    permanentDeleteTarget?.let { doc ->
        DeleteConfirmationDialog(
            title = "Delete Permanently?",
            message = "Permanently delete \"${doc.title}\"? This action cannot be undone.",
            isPermanent = true,
            onConfirm = {
                scope.launch {
                    repository.permanentlyDeleteDocument(doc.id)
                    permanentDeleteTarget = null
                }
            },
            onDismiss = { permanentDeleteTarget = null }
        )
    }
}

@Composable
private fun DeletedDocumentItem(
    document: Document,
    onRestore: () -> Unit,
    onDeletePermanent: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = document.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Deleted: ${document.deletedAt?.let { dateFormat.format(Date(it)) } ?: "Recently"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onDeletePermanent,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete Forever")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onRestore) {
                    Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Restore")
                }
            }
        }
    }
}
