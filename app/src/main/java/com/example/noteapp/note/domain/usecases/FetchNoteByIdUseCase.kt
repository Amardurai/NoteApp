package com.example.noteapp.note.domain.usecases

import com.example.noteapp.note.data.model.NoteEntity
import com.example.noteapp.note.data.model.toNote
import com.example.noteapp.note.domain.model.Note
import com.example.noteapp.note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.flow

class FetchNoteByIdUseCase(private val repository: NoteRepository) {
    operator fun invoke(noteId: Int) = flow {
        emit(repository.fetchNoteById(noteId)?.toNote())
    }
}