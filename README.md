# ProWrite

ProWrite is a modern, offline-first Android word-processing application built with Kotlin and Jetpack Compose, inspired by the core document-editing experience of Microsoft Word.

## Features

- **Document Management**: Create, edit, rename, duplicate, search, sort, and organize documents into folders.
- **Rich-Text Editor**: Page-like paper canvas, live word and character counters, heading styles (Title, H1, H2, H3), bold, italic, underline, strikethrough, bulleted and numbered lists, and clear formatting.
- **Auto-Save & Undo/Redo**: Non-blocking auto-saving to local storage with visual status indicators, along with history-backed undo/redo.
- **Find & Replace**: In-editor match highlighting with case sensitivity, previous/next match navigation, replace current, and replace all.
- **Template Gallery**: Pre-loaded templates for resumes, formal letters, academic assignments, research notes, meeting notes, project status reports, and blank documents.
- **Folders & Organization**: Folder creation, document categorization, favorites pinning, archiving, and multi-selection batch actions.
- **Recycle Bin**: Safe soft-deletion with restoration and permanent wipe options.
- **Export, Print & Share**: Native export to PDF, semantic HTML, and plain text (TXT), system printing via Android Print Framework, and direct document sharing.
- **Material 3 Design System**: Dynamic color theming, dark and light theme support, scalable typography, responsive adaptive layouts, and accessible touch targets.

## Architecture

- **Platform**: Native Android (Kotlin 2.2+, Jetpack Compose, Material 3)
- **Architecture**: Clean Architecture with MVVM and Unidirectional Data Flow
- **Persistence**: Local SQLite storage with reactive Kotlin Flow streams and DataStore Preferences
- **Build System**: Gradle Kotlin DSL with Version Catalog (`gradle/libs.versions.toml`)
