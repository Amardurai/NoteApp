package com.example.noteapp.note.presentation.add_edit_notes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.noteapp.di.UseCases
import com.example.noteapp.note.domain.model.Note
import com.example.noteapp.util.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNotesViewModel @Inject constructor(
    private val useCases: UseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var currentNoteId: Int? = null

    private val _addEditNoteState = MutableStateFlow(AddEditNoteState())
    val addEditNoteState = _addEditNoteState.asStateFlow()

    private val _eventFlow = Channel<AddEditUiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    init {
        viewModelScope.launch {
            loadNoteIfAvailable()
        }
    }

    fun onEvent(event: AddEditNoteAction) {
        when (event) {
            is AddEditNoteAction.EnteredTitle -> {
                _addEditNoteState.update { it.copy(title = event.value) }
            }

            is AddEditNoteAction.EnteredContent -> {
                _addEditNoteState.update { it.copy(content = event.value) }
            }

            is AddEditNoteAction.PinNote -> {
                _addEditNoteState.update { it.copy(isPinned = event.value) }
            }

            is AddEditNoteAction.SaveNote -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val note = addEditNoteState.value

                        if (note.title.isEmpty() && note.content.isEmpty()) {
                            throw Exception("Empty note discarded.")
                        }

                        useCases.insertNoteUseCase.invoke(
                            Note(
                                id = currentNoteId,
                                title = note.title.trim(),
                                content = note.content.trim(),
                                timestamp = System.currentTimeMillis(),
                                isPinned = note.isPinned
                            )
                        )
                    } catch (e: Exception) {
                        _eventFlow.send(
                            AddEditUiEvent.ShowToast(
                                message = e.message ?: "Couldn't save note"
                            )
                        )
                    }
                    _eventFlow.send(AddEditUiEvent.SaveNote)
                }
            }
        }
    }

    private suspend fun loadNoteIfAvailable() {
        _addEditNoteState.update { it.copy(isLoading = true) }
        val noteId = savedStateHandle.toRoute<Screen.AddEditNotesScreen>()
        if (noteId.id != -1) {
            delay(2000)
            useCases.fetchNoteByIdUseCase(noteId.id)?.let { note ->
                currentNoteId = note.id
                _addEditNoteState.value = _addEditNoteState.value.copy(
                    title = note.title,
                    content = note.content,
                    isPinned = note.isPinned,
                    isLoading = false
                )
            }
        } else {
            _addEditNoteState.value = _addEditNoteState.value.copy(isLoading = false)
        }
    }
}