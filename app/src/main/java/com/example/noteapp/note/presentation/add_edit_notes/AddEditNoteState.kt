package com.example.noteapp.note.presentation.add_edit_notes

import androidx.compose.runtime.Stable

@Stable
data class AddEditNoteState(
    val title: String = "",
    val content: String = "",
    val isPinned: Boolean = false,
    val isLocked: Boolean = false,
    val backgroundColor: Int = -1
)
