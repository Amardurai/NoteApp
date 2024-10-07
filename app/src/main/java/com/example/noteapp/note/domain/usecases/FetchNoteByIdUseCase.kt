package com.example.noteapp.note.domain.usecases

import com.example.noteapp.note.data.model.NoteEntity
import com.example.noteapp.note.data.model.toNote
import com.example.noteapp.note.domain.model.Note
import com.example.noteapp.note.domain.repository.NoteRepository

class FetchNoteByIdUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(noteId: Int): NoteEntity? {
        return repository.fetchNoteById(noteId)
    }
}