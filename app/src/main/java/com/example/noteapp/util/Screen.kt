package com.example.noteapp.util

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    object NotesScreen
    @Serializable
    data class AddEditNotesScreen(val id: Int)
}