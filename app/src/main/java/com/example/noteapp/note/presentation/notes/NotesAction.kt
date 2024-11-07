package com.example.noteapp.note.presentation.notes

import com.example.noteapp.note.domain.model.Note

sealed interface NotesAction {
    data object GetAllNotes : NotesAction
    data object RestoreNote : NotesAction
    data object AddNoteClick: NotesAction
    data class NoteClicked(val noteId: Int): NotesAction
    data class DeleteNote(val note: Note) : NotesAction
}
