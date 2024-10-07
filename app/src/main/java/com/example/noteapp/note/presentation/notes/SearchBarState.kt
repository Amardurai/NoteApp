package com.example.noteapp.note.presentation.notes

sealed class SearchBarState {
    data object OPENED : SearchBarState()
    data object CLOSED : SearchBarState()
}
