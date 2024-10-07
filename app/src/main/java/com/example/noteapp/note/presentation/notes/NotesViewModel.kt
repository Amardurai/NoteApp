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

    private val _notesFlow = MutableStateFlow(NotesState())
    val notesFlow = _notesFlow.asStateFlow()

    private var getNotesJob: Job? = null

    init {
        getNotes()
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

            is NotesAction.GetAllNotes -> {
                getNotes()
            }

            is NotesAction.SearchNotes -> {
                searchNote(event.searchText)
            }

            NotesAction.CloseSearchBar -> _notesFlow.update { it.copy(searchBarState = SearchBarState.CLOSED) }
            NotesAction.OpenSearchBar -> _notesFlow.update { it.copy(searchBarState = SearchBarState.OPENED) }
            else -> Unit
        }
    }

    private fun getNotes() {
        getNotesJob?.cancel()
        getNotesJob = useCases.getAllNotesUseCase().onEach { notes ->
                _notesFlow.update { it.copy(notes = notes)
                }
            }.launchIn(viewModelScope)
    }

    private fun searchNote(searchText: String) {
        getNotesJob?.cancel()
        getNotesJob = viewModelScope.launch {
            delay(500)
            useCases.searchNoteUseCase(searchText)
                .onEach { notes ->
                    _notesFlow.update { it.copy(notes = notes) }
                }.launchIn(viewModelScope)
        }
    }

}

