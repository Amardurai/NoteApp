package com.example.noteapp.di

import com.example.noteapp.note.domain.usecases.DeleteNoteUsesCase
import com.example.noteapp.note.domain.usecases.FetchNoteByIdUseCase
import com.example.noteapp.note.domain.usecases.GetAllNotesUseCase
import com.example.noteapp.note.domain.usecases.InsertNoteUseCase

data class UseCases(
    val getAllNotesUseCase: GetAllNotesUseCase,
    val deleteNote: DeleteNoteUsesCase,
    val insertNoteUseCase: InsertNoteUseCase,
    val fetchNoteByIdUseCase: FetchNoteByIdUseCase
)
