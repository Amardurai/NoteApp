package com.example.noteapp.note.presentation.add_edit_notes.mvi


data class EditNoteState(
    val title: String = "",
    val content: String = "",
    val isPinned: Boolean = false,
    val isLoading: Boolean = false,
)
