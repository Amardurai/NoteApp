package com.example.noteapp.note.presentation.notes


sealed interface NotesUiEvent {
    data class ShowSnackBar(val message: String) : NotesUiEvent
}