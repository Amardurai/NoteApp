package com.example.noteapp.note.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.di.UseCases
import com.example.noteapp.note.data.model.toNoteEntity
import com.example.noteapp.note.data.model.toNoteList
import com.example.noteapp.note.domain.model.Note
import com.example.noteapp.note.domain.model.getDeletedNoteMessage
import com.example.noteapp.note.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val useCases: UseCases
) : ViewModel() {

    private var recentlyDeletedNote: Note? = null

    private val _eventFlow = MutableSharedFlow<NotesUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes = _notes.asStateFlow()

    init {
        getNotes()
    }

    private fun getNotes() {
        useCases.getAllNotesUseCase.invoke().onEach { notes ->
            _notes.value = notes
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: NotesAction) {
        when (event) {
            is NotesAction.DeleteNote -> {
                viewModelScope.launch(Dispatchers.IO) {
                    recentlyDeletedNote = event.note
                    useCases.deleteNote(event.note)
                    _eventFlow.emit(NotesUiEvent.ShowSnackBar(message = event.note.getDeletedNoteMessage()))
                }
            }

            is NotesAction.RestoreNote -> {
                viewModelScope.launch(Dispatchers.IO) {
                    recentlyDeletedNote?.let { deletedNote ->
                        useCases.insertNoteUseCase(note = deletedNote)
                        recentlyDeletedNote = null
                    }
                }
            }

            else -> Unit
        }
    }

}

