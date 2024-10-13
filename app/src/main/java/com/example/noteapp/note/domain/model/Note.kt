package com.example.noteapp.note.domain.model

data class Note(
    val id: Int? = null,
    val title: String,
    val content: String,
    val timestamp: Long,
    val isPinned: Boolean,
)

fun Note.getDeletedNoteMessage(): String {
    val noteTitle = title.take(40)
    return if (noteTitle.length >= 40) "$noteTitle... deleted" else "$noteTitle deleted"
}
