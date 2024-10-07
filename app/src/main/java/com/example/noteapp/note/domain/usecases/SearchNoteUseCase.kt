package com.example.noteapp.note.domain.usecases

import androidx.room.Query
import com.example.noteapp.note.data.model.toNoteList
import com.example.noteapp.note.domain.model.Note
import com.example.noteapp.note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchNoteUseCase(private val repository: NoteRepository) {
    operator fun invoke(query: String): Flow<List<Note>> {
        return repository.findNotesByQuery(query).map { it.toNoteList() }
    }
}