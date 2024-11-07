package com.example.noteapp.note.presentation.add_edit_notes

sealed interface AddEditNoteAction {
    data class EnteredTitle(val value: String) : AddEditNoteAction
    data class EnteredContent(val value: String) : AddEditNoteAction
    data class PinNote(val value: Boolean) : AddEditNoteAction
    data object SaveNote : AddEditNoteAction
}
