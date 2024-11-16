package com.example.noteapp.note.presentation.add_edit_notes.mvi

sealed interface EditUiEvent {
    data class ShowToast(val message: String) : EditUiEvent
    data object GoBack : EditUiEvent
}