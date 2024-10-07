package com.example.noteapp.note.domain.usecases

import com.example.noteapp.note.data.model.toNoteEntity
import com.example.noteapp.note.domain.model.Note
import com.example.noteapp.note.domain.repository.NoteRepository

class InsertNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        val noteEntity = note.toNoteEntity()
        repository.insertNote(noteEntity)
    }
}