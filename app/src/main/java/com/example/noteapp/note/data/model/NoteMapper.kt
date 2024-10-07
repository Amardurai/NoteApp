package com.example.noteapp.note.data.model

import com.example.noteapp.note.domain.model.Note

fun NoteEntity.toNote() : Note {
    return Note(
        id = id,
        title = title,
        content = content,
        timestamp = timestamp,
        color = backgroundColor,
        isPinned = isPinned
    )
}

fun Note.toNoteEntity() : NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        timestamp = timestamp,
        backgroundColor = color,
        isPinned = isPinned,
    )
}

fun List<NoteEntity>.toNoteList() : List<Note>{
    return this.map {
        it.toNote()
    }
}