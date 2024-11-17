package com.example.noteapp.note.domain.model

import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class NotePreviewParameterProvider : PreviewParameterProvider<Note> {
    override val values: Sequence<Note> = sequenceOf(
        Note(
            id = 1,
            title = "Kotlin Best Practices",
            content = "1. Utilize extension functions to enhance readability and maintainability in your code.",
            isPinned = true,
            timestamp = System.currentTimeMillis()
        ),
        Note(
            id = 1,
            title = "Compose Performance",
            content = "",
            isPinned = false,
            timestamp = System.currentTimeMillis()
        )
    )

}