package com.prowrite.editor.ui.editor

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatClear
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prowrite.editor.core.model.HeadingLevel
import com.prowrite.editor.core.model.SaveStatus
import com.prowrite.editor.core.util.ExportUtils
import com.prowrite.editor.ui.components.DocumentDetailsBottomSheet
import com.prowrite.editor.ui.components.RenameDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showHeadingMenu by remember { mutableStateOf(false) }
    val detailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Save before exiting on system back
    BackHandler {
        viewModel.saveImmediately()
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.saveImmediately()
                            onBack()
                        },
                        modifier = Modifier.testTag("editor_back_btn")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Column(
                        modifier = Modifier
                            .clickable { viewModel.showRenameDialog(true) }
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            when (state.saveStatus) {
                                SaveStatus.SAVED -> {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Saved",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                SaveStatus.SAVING -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(10.dp),
                                        strokeWidth = 1.5.dp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Saving...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                SaveStatus.UNSAVED_CHANGES -> {
                                    Text(
                                        text = "● Unsaved changes",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                SaveStatus.ERROR -> {
                                    Text(
                                        text = "Save error",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = state.canUndo
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (state.canUndo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = state.canRedo
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (state.canRedo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Editor options")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Find & Replace") },
                                leadingIcon = { Icon(Icons.Default.FindReplace, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.toggleFindReplace()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Document Details & Export") },
                                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.showDetailsSheet(true)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Print") },
                                leadingIcon = { Icon(Icons.Default.Print, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    state.document?.let { ExportUtils.printDocument(context, it) }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Share") },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    state.document?.let { ExportUtils.shareDocument(context, it) }
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Formatting Toolbar
                FormattingToolbar(
                    onBold = { viewModel.applyFormatting("**") },
                    onItalic = { viewModel.applyFormatting("*") },
                    onUnderline = { viewModel.applyFormatting("<u>", "</u>") },
                    onStrikethrough = { viewModel.applyFormatting("~~") },
                    onSelectHeading = { viewModel.applyHeading(it) },
                    onBulletList = { viewModel.applyList(isNumbered = false) },
                    onNumberedList = { viewModel.applyList(isNumbered = true) },
                    onClearFormatting = { viewModel.clearFormatting() }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Document statistics bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${state.wordCount} words · ${state.characterCount} chars",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "~${state.readingTimeMin} min read",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Find and Replace Bar
            AnimatedVisibility(visible = state.findReplace.isActive) {
                FindReplaceBar(
                    state = state.findReplace,
                    onQueryChange = { viewModel.updateFindQuery(it) },
                    onReplaceChange = { viewModel.updateReplaceQuery(it) },
                    onFindNext = { viewModel.findNext() },
                    onFindPrevious = { viewModel.findPrevious() },
                    onReplaceCurrent = { viewModel.replaceCurrent() },
                    onReplaceAll = { viewModel.replaceAll() },
                    onClose = { viewModel.toggleFindReplace() }
                )
            }

            // Document Canvas (Paper-like container centered for comfortable reading and editing)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 760.dp)
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                        .heightIn(min = 600.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        BasicTextField(
                            value = state.textFieldValue,
                            onValueChange = { viewModel.onContentChange(it) },
                            textStyle = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 16.sp,
                                lineHeight = 26.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("editor_text_input")
                        )

                        if (state.textFieldValue.text.isEmpty()) {
                            Text(
                                text = "Start typing your document here...\nUse the formatting toolbar below for headings, bold, lists, and more.",
                                style = TextStyle(
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 16.sp,
                                    lineHeight = 26.sp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Rename Dialog
    if (state.showRenameDialog) {
        RenameDialog(
            initialTitle = state.title,
            onConfirm = {
                viewModel.onTitleChange(it)
                viewModel.showRenameDialog(false)
            },
            onDismiss = { viewModel.showRenameDialog(false) }
        )
    }

    // Document Details Sheet
    if (state.showDetailsSheet && state.document != null) {
        val doc = state.document!!.copy(
            title = state.title,
            content = state.textFieldValue.text,
            wordCount = state.wordCount,
            characterCount = state.characterCount
        )
        DocumentDetailsBottomSheet(
            document = doc,
            sheetState = detailsSheetState,
            onDismiss = { viewModel.showDetailsSheet(false) },
            onExportPdf = { ExportUtils.exportToPdf(context, doc) },
            onExportHtml = { ExportUtils.exportToHtml(context, doc) },
            onExportTxt = { ExportUtils.exportToTxt(context, doc) },
            onPrint = { ExportUtils.printDocument(context, doc) },
            onShare = { ExportUtils.shareDocument(context, doc) }
        )
    }
}

@Composable
private fun FormattingToolbar(
    onBold: () -> Unit,
    onItalic: () -> Unit,
    onUnderline: () -> Unit,
    onStrikethrough: () -> Unit,
    onSelectHeading: (HeadingLevel) -> Unit,
    onBulletList: () -> Unit,
    onNumberedList: () -> Unit,
    onClearFormatting: () -> Unit
) {
    var showHeadingMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBold, modifier = Modifier.size(38.dp)) {
            Icon(Icons.Default.FormatBold, contentDescription = "Bold", modifier = Modifier.size(20.dp))
        }
        IconButton(onClick = onItalic, modifier = Modifier.size(38.dp)) {
            Icon(Icons.Default.FormatItalic, contentDescription = "Italic", modifier = Modifier.size(20.dp))
        }
        IconButton(onClick = onUnderline, modifier = Modifier.size(38.dp)) {
            Icon(Icons.Default.FormatUnderlined, contentDescription = "Underline", modifier = Modifier.size(20.dp))
        }
        IconButton(onClick = onStrikethrough, modifier = Modifier.size(38.dp)) {
            Icon(Icons.Default.FormatStrikethrough, contentDescription = "Strikethrough", modifier = Modifier.size(20.dp))
        }

        Box {
            TextButton(
                onClick = { showHeadingMenu = true },
                modifier = Modifier.height(38.dp)
            ) {
                Text("Heading", style = MaterialTheme.typography.labelMedium)
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            DropdownMenu(
                expanded = showHeadingMenu,
                onDismissRequest = { showHeadingMenu = false }
            ) {
                HeadingLevel.entries.forEach { level ->
                    DropdownMenuItem(
                        text = { Text(level.label) },
                        onClick = {
                            showHeadingMenu = false
                            onSelectHeading(level)
                        }
                    )
                }
            }
        }

        IconButton(onClick = onBulletList, modifier = Modifier.size(38.dp)) {
            Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullet List", modifier = Modifier.size(20.dp))
        }
        IconButton(onClick = onNumberedList, modifier = Modifier.size(38.dp)) {
            Icon(Icons.Default.FormatListNumbered, contentDescription = "Numbered List", modifier = Modifier.size(20.dp))
        }
        IconButton(onClick = onClearFormatting, modifier = Modifier.size(38.dp)) {
            Icon(Icons.Default.FormatClear, contentDescription = "Clear formatting", modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun FindReplaceBar(
    state: com.prowrite.editor.core.model.FindReplaceState,
    onQueryChange: (String) -> Unit,
    onReplaceChange: (String) -> Unit,
    onFindNext: () -> Unit,
    onFindPrevious: () -> Unit,
    onReplaceCurrent: () -> Unit,
    onReplaceAll: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.findQuery,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Find...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(onClick = onFindPrevious) {
                    Icon(Icons.Default.NavigateBefore, contentDescription = "Previous match")
                }
                IconButton(onClick = onFindNext) {
                    Icon(Icons.Default.NavigateNext, contentDescription = "Next match")
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close find")
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.replaceQuery,
                    onValueChange = onReplaceChange,
                    placeholder = { Text("Replace with...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                TextButton(onClick = onReplaceCurrent) {
                    Text("Replace")
                }
                TextButton(onClick = onReplaceAll) {
                    Text("All")
                }
            }

            if (state.findQuery.isNotBlank()) {
                val matchText = if (state.matchIndices.isNotEmpty()) {
                    "${state.currentMatchIndex + 1} of ${state.matchIndices.size} matches"
                } else {
                    "No matches found"
                }
                Text(
                    text = matchText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
        }
    }
}
