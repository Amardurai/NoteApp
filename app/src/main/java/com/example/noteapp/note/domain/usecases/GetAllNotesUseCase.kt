package com.example.noteapp.note.domain.usecases

import com.example.noteapp.note.data.model.toNoteList
import com.example.noteapp.note.domain.model.Note
import com.example.noteapp.note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.fetchNotes().map { it.toNoteList() }
    }
}