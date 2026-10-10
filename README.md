# word
Act as a senior Android developer, software architect, Jetpack Compose expert, and professional mobile UI/UX designer.

Create a complete, modern, professional Android word-processing application inspired by the core document-editing experience of Microsoft Word.

IMPORTANT:
Do not copy Microsoft Word branding, logos, proprietary assets, copyrighted icons, exact layouts, or visual identity. Create an original application with its own name, design system, color palette, interface, and user experience.

APP INFORMATION

App name: ProWrite
Application ID: com.prowrite.editor
Platform: Native Android
Programming language: Kotlin
UI framework: Jetpack Compose
Design system: Material 3
Minimum SDK: 26
Target SDK: Latest stable Android SDK
Architecture: Clean Architecture with MVVM
Build system: Gradle Kotlin DSL
Dependency management: Version Catalog
Asynchronous programming: Kotlin Coroutines and Flow
Local database: Room
Settings storage: DataStore Preferences
Dependency injection: Hilt
Navigation: Navigation Compose
Background tasks: WorkManager
Image loading: Coil Compose, only where genuinely needed
Testing: JUnit, MockK, Room Testing and Compose UI Testing

PRIMARY OBJECTIVE

Build a complete offline-first Android document editor that allows users to create, edit, format, organize, search, import, export, print and share documents through a clean, responsive and professional user interface.

The application must feel fast, modern, polished and suitable for students, teachers, writers, researchers and office users.

The application must work offline. Documents must be saved locally by default. Do not add cloud storage, analytics, advertisements or online accounts in the initial version.

==================================================
1. PROFESSIONAL FRONTEND AND UI/UX REQUIREMENTS
==================================================

Create the complete frontend using Jetpack Compose and Material 3.

The interface must be:

- Modern
- Minimal
- Professional
- Original
- Responsive
- Accessible
- Smooth
- Consistent
- Easy to understand
- Suitable for both new and experienced users

Do not create a basic demo-style interface.

The UI must look like a production application that can be published on Google Play Store.

Use:

- Material 3 components
- Edge-to-edge layouts
- Predictive back navigation where supported
- WindowInsets for status bar, navigation bar and keyboard
- Adaptive layouts for phones and tablets
- NavigationRail or permanent navigation drawer on large screens
- Bottom navigation or modal navigation drawer on smaller screens
- Scaffold, TopAppBar, BottomAppBar, FloatingActionButton and SnackbarHost
- ModalBottomSheet for context-based actions
- Dialogs only when user confirmation is necessary
- LazyColumn and LazyVerticalGrid for displaying documents and templates
- AnimatedVisibility and animateContentSize for meaningful transitions
- Shared design system for colors, typography, shapes, spacing and icons

Avoid:

- Overcrowded toolbars
- Very large empty spaces
- Excessive animations
- Random colors
- Hardcoded dimensions inside composables
- Hardcoded user-facing text
- Tiny buttons
- Unclear icons
- Deep navigation
- Unnecessary confirmation dialogs
- Business logic inside composables

==================================================
2. DESIGN SYSTEM
==================================================

Create a complete reusable design system.

Create separate files for:

- Color.kt
- Type.kt
- Shape.kt
- Theme.kt
- Dimens.kt
- AppIcons.kt
- AppSpacing.kt

COLOR PALETTE

Use an original professional color palette.

Suggested primary colors:

Light theme:
- Primary: Deep indigo or royal blue
- Secondary: Soft teal
- Background: Light neutral gray
- Surface: White
- Error: Accessible red
- Success: Professional green
- Warning: Warm amber

Dark theme:
- Primary: Soft blue
- Secondary: Muted teal
- Background: Near-black blue-gray
- Surface: Dark gray-blue
- Error: Soft red
- Success: Soft green
- Warning: Soft amber

Requirements:

- Support light theme
- Support dark theme
- Support system default theme
- Support Material You dynamic colors on compatible devices
- All color combinations must have accessible contrast
- Do not use color as the only indicator of status
- Provide selected, disabled, pressed, focused and hover states

TYPOGRAPHY

Use Material 3 typography with a professional and highly readable font.

Define styles for:

- Display title
- Screen title
- Section heading
- Card title
- Document title
- Document preview
- Body text
- Supporting text
- Caption
- Button text
- Editor content
- Toolbar labels

Use scalable typography and support large system font sizes without breaking the layout.

SHAPES

Use consistent rounded corners:

- Small components: 8dp
- Input fields and buttons: 12dp
- Cards: 16dp
- Large containers and bottom sheets: 20dp to 28dp

SPACING

Create reusable spacing values:

- Extra small: 4dp
- Small: 8dp
- Medium: 12dp
- Default: 16dp
- Large: 24dp
- Extra large: 32dp

Do not place random spacing values directly inside screens.

==================================================
3. SPLASH SCREEN
==================================================

Create a modern splash screen using the Android SplashScreen API.

The splash screen should contain:

- Original ProWrite app icon
- App name
- Short tagline such as “Write. Format. Create.”
- Theme-aware background

Requirements:

- Do not keep the splash screen visible unnecessarily
- Load only essential initialization data
- Navigate to onboarding for first-time users
- Navigate to Home for returning users

==================================================
4. ONBOARDING SCREENS
==================================================

Create a short three-page onboarding experience.

Page 1:
- Create and edit documents
- Minimal original illustration or Material icon composition

Page 2:
- Format text professionally
- Show a visual representation of formatting tools

Page 3:
- Save, export, print and share
- Clear Get Started button

Onboarding requirements:

- Horizontal pager
- Page indicators
- Skip button
- Next button
- Get Started button
- Save onboarding-completed state in DataStore
- Support light and dark themes
- Add content descriptions
- Do not show onboarding after it has been completed

==================================================
5. HOME SCREEN
==================================================

Create a polished Home screen.

Home screen structure:

Top section:
- Greeting or “Your Documents” heading
- Search icon
- User preferences or settings icon
- Optional navigation drawer button

Search section:
- Expandable professional search field
- Search by document title and document content
- Clear search button
- Show recent search suggestions
- Debounce search input

Quick actions:
- New blank document
- Create from template
- Import document
- Scan or image-to-text entry point as a future disabled or planned feature

Document categories:
- Recent
- Favorites
- All Documents
- Folders
- Shared or Exported
- Recycle Bin

Recent documents section:
- Horizontal cards for recently edited documents
- Display title, preview, last edited time and file type
- Show an overflow menu
- Highlight favorite documents

All documents section:
- Switch between list and grid layouts
- Sort documents by:
  - Name
  - Last modified date
  - Created date
  - Word count
  - File size
- Filter by:
  - Recent
  - Favorites
  - Archived
  - Folder
- Store selected layout and sorting option in DataStore

Floating Action Button:
- Use an ExtendedFloatingActionButton when appropriate
- Label it “New Document”
- On click, open a modal bottom sheet containing:
  - Blank document
  - Choose template
  - Import text file

Document card requirements:
- Document icon or preview
- Document title
- Two-line content preview
- Last modified date
- Word count
- Favorite indicator
- Overflow menu
- Selected state for multi-selection
- Long press to activate selection mode

Overflow menu actions:
- Open
- Rename
- Duplicate
- Add to favorites
- Move to folder
- Archive
- Export
- Share
- Delete

Home screen states:
- Loading state with skeleton placeholders
- Empty state with illustration and “Create Your First Document” action
- Search-empty state
- Database error state
- Offline state only if an online feature is added later
- Success snackbar after actions
- Undo snackbar after deleting or archiving a document

==================================================
6. DOCUMENT EDITOR SCREEN
==================================================

Create a powerful but distraction-free document editing screen.

EDITOR TOP APP BAR

The TopAppBar must contain:

- Back button
- Editable or tappable document title
- Save status
- Undo button
- Redo button
- Overflow menu

Save statuses:

- Saved
- Saving
- Unsaved changes
- Save failed

Represent status using both icon and text where space allows.

The overflow menu should include:

- Find and replace
- Document details
- Page setup
- Export
- Print
- Share
- Save a copy
- Move to folder
- Delete

EDITOR CANVAS

Create a professional editor canvas.

Requirements:

- White page-like surface in light theme
- Dark readable editing surface in dark theme
- Comfortable horizontal and vertical padding
- Rounded page edges where appropriate
- Subtle elevation only on large-screen or page-preview layouts
- Full-width editing mode on smaller phones
- Centered page mode on tablets
- Smooth vertical scrolling
- Cursor must remain visible above the keyboard
- Support very long documents
- Avoid resetting cursor position after recomposition
- Preserve selection and formatting state
- Display placeholder text for empty documents
- Support scrolling while text is selected

Text editor must support:

- Plain text input
- Rich-text spans or a structured rich-text document model
- Bold
- Italic
- Underline
- Strikethrough
- Text color
- Highlight color
- Font size
- Heading styles
- Text alignment
- Bulleted list
- Numbered list
- Indentation
- Clear formatting

If full rich-text support is difficult with the standard BasicTextField, create a maintainable editor abstraction.

Do not fake formatting by changing the entire document style when only selected text should change.

FORMAT TOOLBAR

Create a keyboard-aware formatting toolbar.

On phones:
- Show a compact horizontally scrollable toolbar above the keyboard or in a BottomAppBar
- Open additional formatting options in a ModalBottomSheet

On tablets:
- Show an expanded formatting toolbar
- Optionally show a formatting side panel

Toolbar controls:

- Bold
- Italic
- Underline
- Strikethrough
- Heading style
- Font size
- Text color
- Highlight color
- Alignment
- Bulleted list
- Numbered list
- Increase indent
- Decrease indent
- Clear formatting

Requirements:

- Selected formatting controls must display an active state
- Disabled options must have a visible disabled state
- Each control must have a tooltip or accessible label
- Do not hide the editor behind the keyboard or toolbar
- Group related tools logically
- Use icons plus optional labels based on available screen width

EDITOR BOTTOM INFORMATION BAR

Display:

- Current word count
- Character count
- Current page or section when page support is available
- Save status

Allow users to hide this bar from settings.

==================================================
7. DOCUMENT TITLE EDITING
==================================================

When the user taps the title:

- Open a clean rename dialog or inline title field
- Preselect the existing title
- Validate empty titles
- Prevent invalid file-name characters when necessary
- Display inline error messages
- Save after pressing Done
- Support keyboard IME action

If a title is empty, generate a localized default title such as “Untitled Document”.

==================================================
8. FIND AND REPLACE UI
==================================================

Create a find and replace interface inside the editor.

Requirements:

- Search field
- Replace field
- Previous match
- Next match
- Replace current
- Replace all
- Match count
- Case-sensitive option
- Close button
- Highlight search results
- Keep the currently selected result visually distinct
- Open as a top overlay or professional bottom sheet
- Preserve editor content and current selection

==================================================
9. NEW DOCUMENT EXPERIENCE
==================================================

When the user selects New Document, display a bottom sheet with:

- Blank document
- Resume template
- Formal letter template
- Assignment template
- Research notes template
- Meeting notes template
- Project report template
- Import existing file

After selecting a template:

- Create a new document
- Insert template structure
- Open it in the editor
- Focus the first editable section
- Trigger auto-save

==================================================
10. TEMPLATE GALLERY
==================================================

Create a professional template gallery.

Template gallery requirements:

- Responsive grid
- Search templates
- Filter by category
- Template preview card
- Template name
- Short description
- Category
- Use Template button
- Preview screen before creating the document

Categories:

- Academic
- Professional
- Personal
- Business
- Notes
- Reports

Required templates:

- Blank document
- Resume
- Formal letter
- Assignment
- Research notes
- Meeting notes
- Project report

Use static local templates initially.

==================================================
11. SEARCH SCREEN
==================================================

Create a dedicated search screen.

Features:

- Autofocused search field
- Search documents by title and content
- Recent searches
- Clear search history
- Filter chips
- Sort options
- Highlight matching title or content text
- Display the matching text snippet
- Show result count
- Empty search state
- No-results state
- Remove a recent search item
- Debounced search
- Back navigation

Search must run outside the main thread.

==================================================
12. FOLDER MANAGEMENT SCREEN
==================================================

Create a folder management interface.

Features:

- Create folder
- Rename folder
- Delete folder
- Move documents between folders
- Folder document count
- Last updated date
- Folder list and grid views
- Folder color selection
- Empty-folder state
- Multi-select documents
- Breadcrumb navigation where appropriate

Do not delete documents automatically when a folder is deleted. Move them to All Documents or ask the user where they should go.

==================================================
13. FAVORITES AND ARCHIVE
==================================================

Create separate filtered views for:

- Favorite documents
- Archived documents

Requirements:

- Reuse the document-card design
- Provide sorting and filtering
- Allow users to remove documents from favorites
- Allow users to restore archived documents
- Show useful empty states

==================================================
14. RECYCLE BIN SCREEN
==================================================

Create a professional recycle-bin screen.

Features:

- Display deleted documents
- Show deletion date
- Restore document
- Permanently delete document
- Empty recycle bin
- Multi-select actions
- Confirmation dialog before permanent deletion
- Undo action after moving a document to recycle bin

Confirmation dialog must clearly explain that permanent deletion cannot be undone.

==================================================
15. SETTINGS SCREEN
==================================================

Create an organized Settings screen.

Use grouped preference sections.

Appearance:
- Light theme
- Dark theme
- System theme
- Dynamic color toggle
- List or grid default view
- Editor page appearance

Editor:
- Default font size
- Default paragraph style
- Auto-save interval
- Show word count
- Show character count
- Haptic feedback
- Keep screen awake while editing

Export:
- Default export format
- Default PDF page size
- Include document title
- Default file location through Storage Access Framework

Storage:
- Storage usage
- Clear recycle bin
- Export all documents
- Import backup
- Create backup

Privacy:
- Privacy information
- App lock placeholder
- Biometric lock as an optional later enhancement

About:
- App version
- Open-source licenses
- Feedback option
- Privacy policy placeholder

All settings must be stored using DataStore.

==================================================
16. DOCUMENT DETAILS SCREEN OR BOTTOM SHEET
==================================================

Display:

- Document title
- Creation date
- Last modified date
- Last opened date
- Word count
- Character count
- File size
- Folder
- Favorite status
- Archive status
- Export location if available

Provide actions for:

- Rename
- Move
- Duplicate
- Export
- Share
- Delete

==================================================
17. IMPORT, EXPORT, PRINT AND SHARE UI
==================================================

IMPORT

Use Android Storage Access Framework.

Import requirements:

- Import TXT files
- Display file picker
- Display import progress for large files
- Handle empty files
- Handle unsupported encoding
- Handle corrupted files
- Display user-friendly errors
- Allow canceling when possible

EXPORT

Support:

- TXT
- HTML
- PDF

Create an export bottom sheet containing:

- Export format
- File name
- Destination selection
- PDF page size
- Page orientation
- Margins
- Include title option
- Export button
- Progress status
- Success state
- Open file action
- Share file action

Use a proper PDF generation method suitable for Android.

PRINT

Use Android Print Framework.

SHARE

Use Android Sharesheet and FileProvider where required.

Do not request broad file-system permissions.

Structure the code so DOCX import and export can be added later without rewriting the editor architecture.

==================================================
18. DOCUMENT MANAGEMENT
==================================================

Support:

- Create document
- Open document
- Rename document
- Duplicate document
- Favorite document
- Unfavorite document
- Archive document
- Restore archived document
- Move document to folder
- Move document to recycle bin
- Restore deleted document
- Permanently delete document
- Bulk actions
- Document search
- Document sorting
- List and grid layouts

All destructive actions must provide:

- Clear confirmation when necessary
- Snackbar feedback
- Undo option where restoration is possible

==================================================
19. AUTO-SAVE AND CRASH RECOVERY
==================================================

Implement reliable automatic saving.

Requirements:

- Debounce frequent editor changes
- Save outside the main thread
- Display saving status
- Display saved status
- Retry after temporary failures
- Preserve unsaved content in SavedStateHandle or a draft mechanism
- Restore content after unexpected process termination when possible
- Save before exiting the editor
- Avoid writing to Room after every keystroke
- Prevent race conditions between multiple save operations

Allow auto-save interval to be configured in Settings.

==================================================
20. LOADING, EMPTY, ERROR AND SUCCESS STATES
==================================================

Every screen must handle:

- Loading
- Empty content
- Success
- Recoverable error
- Non-recoverable error

Use:

- Skeleton placeholders for initial document loading
- Inline progress indicators for small operations
- CircularProgressIndicator for short blocking operations
- Snackbar for action feedback
- Dialogs only for decisions requiring user confirmation
- Retry buttons for recoverable errors

Error messages must explain:

- What happened
- Whether user data is safe
- What action the user can take

Do not display raw exceptions or technical stack traces to the user.

==================================================
21. ANIMATIONS AND MICRO-INTERACTIONS
==================================================

Add subtle, purposeful animations.

Examples:

- FAB expansion
- Document-card appearance
- List-to-grid transition
- Bottom-sheet appearance
- Selection state
- Favorite icon transition
- Toolbar selected states
- Screen navigation
- Empty-state appearance
- Save-status transition

Requirements:

- Keep animations short and professional
- Respect the system’s reduced-motion or animator-duration settings where possible
- Do not animate every component
- Avoid animations that delay document editing

==================================================
22. RESPONSIVE AND ADAPTIVE LAYOUTS
==================================================

Support:

- Small Android phones
- Standard phones
- Foldable devices
- Tablets
- Portrait orientation
- Landscape orientation
- Resizable multi-window mode

Phone layout:

- Bottom navigation or modal drawer
- Single-pane navigation
- Compact editor toolbar
- Full-width editor

Tablet layout:

- Navigation rail or permanent drawer
- Two-pane home interface where appropriate
- Document list on one side and preview or details on the other
- Centered page-like editor
- Expanded formatting controls
- Increased but controlled spacing

Use window-size classes or an equivalent adaptive-layout approach.

Do not simply stretch the phone layout across tablet screens.

==================================================
23. ACCESSIBILITY
==================================================

Implement accessibility from the beginning.

Requirements:

- Content descriptions for icons
- Semantic headings
- Minimum touch target sizes
- Keyboard navigation
- Physical keyboard shortcut support
- High color contrast
- Large-font support
- TalkBack-friendly controls
- Clear focus order
- Do not rely only on colors
- Accessible error messages
- Accessible selected states
- Accessible dialogs and bottom sheets
- Sufficient spacing between actions

Suggested physical keyboard shortcuts:

- Ctrl + S: Save
- Ctrl + B: Bold
- Ctrl + I: Italic
- Ctrl + U: Underline
- Ctrl + Z: Undo
- Ctrl + Shift + Z: Redo
- Ctrl + F: Find
- Ctrl + H: Find and replace
- Ctrl + A: Select all
- Ctrl + P: Print

==================================================
24. FRONTEND COMPONENTS
==================================================

Create reusable Compose components such as:

- ProWriteTopAppBar
- DocumentCard
- DocumentListItem
- DocumentGridItem
- FolderCard
- TemplateCard
- EmptyState
- ErrorState
- LoadingState
- SearchBar
- SortBottomSheet
- FilterBottomSheet
- NewDocumentBottomSheet
- ExportBottomSheet
- DocumentOptionsMenu
- FormattingToolbar
- EditorCanvas
- SaveStatusIndicator
- WordCountBar
- RenameDocumentDialog
- ConfirmDeleteDialog
- ColorPickerBottomSheet
- FontSizeSelector
- HeadingStyleSelector
- SelectionActionBar
- SettingsSection
- SettingsSwitchItem
- SettingsNavigationItem
- AppSnackbar

Every reusable component should:

- Receive state through parameters
- Expose actions through callbacks
- Avoid directly accessing ViewModels
- Include at least one @Preview
- Include a dark-theme preview when visually important
- Support long text and large fonts
- Use the central design system

==================================================
25. COMPOSE STATE MANAGEMENT
==================================================

Use immutable UI state.

Create one UI state class per major screen.

Examples:

- HomeUiState
- EditorUiState
- SearchUiState
- TemplateUiState
- FolderUiState
- RecycleBinUiState
- SettingsUiState
- ExportUiState

Use sealed interfaces or sealed classes for:

- UI events
- Navigation events
- One-time effects
- User messages
- Export results
- Import results

Follow unidirectional data flow:

UI Event -> ViewModel -> Use Case -> Repository -> Data Source
Data Source -> Repository -> ViewModel State -> Compose UI

Do not:

- Perform database calls inside composables
- Store important state only using remember
- Pass NavController into every composable
- Pass ViewModels into reusable components
- Launch duplicate Flow collectors
- Trigger repeated side effects during recomposition

Use:

- collectAsStateWithLifecycle
- rememberSaveable for appropriate temporary UI state
- LaunchedEffect for controlled one-time effects
- StateFlow for persistent screen state
- SharedFlow or Channel for one-time events

==================================================
26. NAVIGATION STRUCTURE
==================================================

Create type-safe navigation where possible.

Required destinations:

- Splash
- Onboarding
- Home
- Editor
- Templates
- Template Preview
- Search
- Folders
- Folder Details
- Favorites
- Archive
- Recycle Bin
- Settings
- Document Details
- Export
- About

Navigation requirements:

- Preserve editor state
- Avoid creating duplicate destinations
- Support deep links to documents where appropriate
- Correctly handle system back
- Warn the user only when truly unsaved changes may be lost
- Restore state after process recreation
- Use clear route arguments
- Do not pass large document content through navigation arguments
- Pass only document IDs between destinations

==================================================
27. DATA MODEL
==================================================

Create suitable Room entities and relationships for:

- DocumentEntity
- DocumentContentEntity
- FolderEntity
- TemplateEntity
- RecentSearchEntity
- ExportHistoryEntity

Each document should include:

- id
- title
- plainTextContent
- richTextContent or structured-content representation
- folderId
- createdAt
- updatedAt
- lastOpenedAt
- deletedAt
- isFavorite
- isArchived
- isDeleted
- wordCount
- characterCount
- fileSize
- exportPath if available

Use:

- Foreign keys
- Proper indices
- TypeConverters
- Database migrations
- Unique constraints where needed
- Transaction support for multi-table operations

Do not store screen-specific UI state inside Room.

==================================================
28. ARCHITECTURE REQUIREMENTS
==================================================

Organize the project into:

- app
- core-common
- core-ui
- core-designsystem
- core-database
- core-datastore
- core-model
- core-navigation
- data
- domain
- feature-onboarding
- feature-home
- feature-editor
- feature-search
- feature-folders
- feature-templates
- feature-recyclebin
- feature-settings
- feature-export

If full Gradle multi-module architecture would make the initial implementation unnecessarily difficult, organize these as clearly separated packages first.

Keep the architecture ready for future modularization.

Use:

- Repository pattern
- Use cases
- Immutable domain models
- UI models
- Mapping functions
- StateFlow
- Sealed interfaces
- SavedStateHandle
- Dependency injection
- Centralized dispatcher provider
- Centralized error handling
- Input validation
- Single source of truth
- Result wrapper where useful

Do not:

- Put business logic inside composables
- Use Room entities directly in UI
- Place all code in a single package
- Create very large ViewModels
- Create God classes
- Use deprecated Android APIs
- Use GlobalScope
- Block the main thread
- Hardcode strings, colors or dimensions
- Add unnecessary third-party libraries

==================================================
29. DOMAIN USE CASES
==================================================

Create use cases including:

- CreateDocumentUseCase
- UpdateDocumentUseCase
- ObserveDocumentUseCase
- ObserveDocumentsUseCase
- SearchDocumentsUseCase
- RenameDocumentUseCase
- DuplicateDocumentUseCase
- ToggleFavoriteUseCase
- ArchiveDocumentUseCase
- RestoreArchivedDocumentUseCase
- DeleteDocumentUseCase
- RestoreDeletedDocumentUseCase
- PermanentlyDeleteDocumentUseCase
- MoveDocumentUseCase
- CreateFolderUseCase
- RenameFolderUseCase
- DeleteFolderUseCase
- ExportDocumentUseCase
- ImportDocumentUseCase
- GetDocumentStatisticsUseCase
- AutoSaveDocumentUseCase

Keep use cases focused and independently testable.

==================================================
30. SECURITY AND PRIVACY
==================================================

Requirements:

- Store documents privately inside app storage by default
- Do not upload documents to any server
- Do not add analytics
- Do not add advertisements
- Do not add tracking SDKs
- Validate imported files
- Limit imported file size where appropriate
- Sanitize generated export file names
- Use secure FileProvider configuration
- Grant temporary URI permissions when sharing
- Do not request MANAGE_EXTERNAL_STORAGE
- Use Storage Access Framework
- Keep biometric app lock as an optional future feature

==================================================
31. TESTING
==================================================

Create:

- Unit tests for use cases
- Unit tests for ViewModels
- Repository tests
- Room DAO tests
- Room migration tests
- DataStore tests where appropriate
- Compose UI tests
- Navigation tests
- Import and export tests
- Auto-save tests

Test scenarios:

- Create document
- Update document
- Auto-save document
- Rename document
- Search document
- Favorite document
- Archive document
- Delete document
- Restore deleted document
- Permanently delete document
- Create and rename folder
- Move document to folder
- Import text file
- Export TXT
- Export HTML
- Export PDF
- Recover draft after failure
- Handle empty states
- Handle database errors

Create fake repositories and fake data sources for:

- Unit tests
- Compose previews
- UI tests

Do not connect previews to the real Room database.

==================================================
32. PERFORMANCE
==================================================

Requirements:

- Avoid unnecessary recompositions
- Use stable and immutable state models
- Use persistent collections if genuinely useful
- Debounce search
- Debounce auto-save
- Perform Room and file operations outside the main thread
- Use lazy lists
- Use keys in LazyColumn and LazyVerticalGrid
- Avoid loading complete document bodies for every Home screen card
- Store or query short document previews separately
- Handle large documents without freezing the UI
- Avoid creating new formatter objects during every recomposition
- Avoid repeatedly parsing complete rich-text content
- Use derivedStateOf only where appropriate
- Use remember for expensive UI calculations
- Add pagination only if genuinely necessary

==================================================
33. STRING RESOURCES AND LOCALIZATION
==================================================

Place all user-facing strings in string resources.

Requirements:

- English as the initial application language
- Make the application localization-ready
- Support Urdu and right-to-left layouts in a future localization phase
- Avoid string concatenation that breaks translation
- Use quantity strings for plural values
- Localize dates and numbers
- Support RTL mirroring where appropriate
- Keep document content independent from the application language

==================================================
34. PREVIEWS
==================================================

Create Compose previews for:

- Light Home screen
- Dark Home screen
- Empty Home screen
- Home loading state
- Home error state
- Document list card
- Document grid card
- Light Editor screen
- Dark Editor screen
- Formatting toolbar
- Template gallery
- Folder card
- Recycle-bin item
- Settings screen
- Export bottom sheet
- Delete confirmation dialog
- Tablet Home layout
- Tablet Editor layout

Use realistic fake preview data.

==================================================
35. IMPLEMENTATION PHASES
==================================================

Build the project in phases.

PHASE 1: FOUNDATION AND HOME FRONTEND

Create:

- Android Studio project setup
- Gradle Kotlin DSL configuration
- Version catalog
- Hilt configuration
- Application class
- Core package structure
- Material 3 theme
- Color system
- Typography
- Shapes
- Dimensions
- Navigation structure
- Room database
- Document entity
- DAO
- Repository
- Domain models
- Basic use cases
- Home ViewModel
- Home UI state and events
- Functional Home screen
- Document list and grid views
- Document creation
- Document search
- Document deletion
- Empty, loading and error states
- Compose previews

PHASE 2: EDITOR FRONTEND AND AUTO-SAVE

Create:

- Editor screen
- Editor ViewModel
- Editor UI state
- Editor canvas
- Formatting toolbar
- Basic rich-text model
- Bold, italic, underline and strikethrough
- Heading styles
- Lists and alignment
- Undo and redo
- Word and character count
- Find and replace
- Save-status indicator
- Automatic saving
- Draft recovery
- Physical keyboard shortcuts

PHASE 3: ORGANIZATION

Create:

- Folders
- Favorites
- Archive
- Recycle bin
- Document details
- Multi-selection
- Bulk actions
- Templates
- Template preview
- Settings
- DataStore preferences

PHASE 4: FILE OPERATIONS

Create:

- TXT import
- TXT export
- HTML export
- PDF export
- Android Print Framework integration
- Android Sharesheet integration
- FileProvider
- Storage Access Framework
- Export UI
- Import UI
- Progress and error states

PHASE 5: QUALITY

Create:

- Unit tests
- DAO tests
- Migration tests
- Repository tests
- ViewModel tests
- Compose UI tests
- Accessibility improvements
- Tablet layouts
- Performance optimization
- Edge-case handling
- Final code cleanup

==================================================
36. OUTPUT FORMAT
==================================================

Follow this exact output process:

1. First provide a brief architecture overview.
2. Show the complete project folder and package structure.
3. List the libraries and explain why each library is needed.
4. Provide the version catalog.
5. Provide root Gradle files.
6. Provide app or module Gradle files.
7. Generate code file by file.
8. Before every code block, display the complete file path.
9. Include all required imports.
10. Provide complete compilable code.
11. Do not provide pseudocode.
12. Do not omit required methods.
13. Do not leave required-feature TODO comments.
14. Do not use deprecated APIs.
15. Explain where every file must be placed.
16. Complete only one phase at a time.
17. At the end of each phase, provide build and verification steps.
18. At the end of each phase, list the completed features.
19. At the end of each phase, list any intentionally deferred features.
20. Before moving to another phase, ensure the current phase compiles.
21. When modifying an existing file, provide its complete updated content.
22. Do not respond with incomplete snippets such as “remaining code is similar”.
23. Do not reduce the requested architecture without explaining the reason.
24. If a dependency causes a compatibility problem, use a stable compatible alternative.
25. Use the latest stable mutually compatible library versions.
26. Avoid alpha, beta and release-candidate dependencies unless absolutely necessary.
27. Ensure the project can open and build in Android Studio.
28. Include commands or steps for running unit tests and Compose UI tests.
29. Add comments only where logic is not self-explanatory.
30. Use realistic fake data only for Compose previews and tests.

==================================================
37. FIRST RESPONSE INSTRUCTION
==================================================

Start with Phase 1 only.

In the first response, generate:

- Architecture overview
- Complete project/package structure
- libs.versions.toml
- Root build.gradle.kts
- settings.gradle.kts
- App build.gradle.kts
- AndroidManifest.xml
- ProWriteApplication class
- Hilt setup
- Dispatcher module
- Material 3 design system
- Light and dark themes
- Dynamic color support
- Navigation graph
- Room database
- Document entity
- Document DAO
- Domain Document model
- Entity-to-domain mappers
- Repository interface
- Repository implementation
- Create document use case
- Observe documents use case
- Search documents use case
- Delete document use case
- Home UI state
- Home UI events
- Home ViewModel
- Functional Home screen
- List and grid layouts
- Document card components
- New document bottom sheet
- Search interface
- Sorting interface
- Empty state
- Loading state
- Error state
- Snackbar feedback
- Compose previews
- Initial unit tests
- Build and verification instructions

Ensure Phase 1 is internally consistent and compilable before ending the response.
