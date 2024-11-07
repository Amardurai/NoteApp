package com.example.noteapp.note.presentation.add_edit_notes

data class AddEditNoteState(
    val title: String = "",
    val content: String = "",
    val isPinned: Boolean = false,
    val isLoading: Boolean = true,
)
