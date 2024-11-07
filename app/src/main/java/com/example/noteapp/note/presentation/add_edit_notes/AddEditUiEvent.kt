package com.example.noteapp.note.presentation.add_edit_notes

sealed interface AddEditUiEvent {
    data class ShowToast(val message: String) : AddEditUiEvent
    data object SaveNote : AddEditUiEvent
}