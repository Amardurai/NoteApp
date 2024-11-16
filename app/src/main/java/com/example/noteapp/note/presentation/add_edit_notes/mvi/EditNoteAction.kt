package com.example.noteapp.note.presentation.add_edit_notes.mvi

sealed interface EditNoteAction {
    data class EnteredTitle(val value: String) : EditNoteAction
    data class EnteredContent(val value: String) : EditNoteAction
    data object TogglePin : EditNoteAction
    data object SaveNote : EditNoteAction
}
