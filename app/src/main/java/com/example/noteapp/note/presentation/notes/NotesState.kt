package com.example.noteapp.note.presentation.notes

import com.example.noteapp.note.domain.model.Note

data class NotesState(
    var notes: List<Note> = emptyList(),
)