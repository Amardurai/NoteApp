package com.example.noteapp.note.domain.repository

import com.example.noteapp.note.data.model.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun fetchNotes(): Flow<List<NoteEntity>>

    suspend fun fetchNoteById(id: Int): NoteEntity?

    suspend fun deleteNote(note: NoteEntity)

    suspend fun insertNote(note: NoteEntity)
}