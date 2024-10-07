package com.example.noteapp.di

import com.example.noteapp.note.domain.usecases.DeleteNoteUsesCase
import com.example.noteapp.note.domain.usecases.FetchNoteByIdUseCase
import com.example.noteapp.note.domain.usecases.GetAllNotesUseCase
import com.example.noteapp.note.domain.usecases.InsertNoteUseCase
import com.example.noteapp.note.domain.usecases.SearchNoteUseCase

data class UseCases(
    val getAllNotesUseCase: GetAllNotesUseCase,
    val deleteNote: DeleteNoteUsesCase,
    val insertNoteUseCase: InsertNoteUseCase,
    val searchNoteUseCase: SearchNoteUseCase,
    val fetchNoteByIdUseCase: FetchNoteByIdUseCase
)
