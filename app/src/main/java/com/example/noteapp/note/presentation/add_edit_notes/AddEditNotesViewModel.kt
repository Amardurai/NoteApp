package com.example.noteapp.note.presentation.add_edit_notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.noteapp.di.UseCases
import com.example.noteapp.note.data.model.toNoteEntity
import com.example.noteapp.note.domain.model.Note
import com.example.noteapp.note.domain.repository.NoteRepository
import com.example.noteapp.util.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNotesViewModel @Inject constructor(
    private val useCases: UseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        val noteId = savedStateHandle.toRoute<Screen.AddEditNotesScreen>()
        if (noteId.id != -1) {
            viewModelScope.launch {
                useCases.fetchNoteByIdUseCase(noteId.id)?.also { note ->
                    currentNoteId = note.id
                    _addEditNoteState.value = _addEditNoteState.value.copy(
                        title = note.title,
                        content = note.content,
                        isPinned = note.isPinned,
                        backgroundColor = note.backgroundColor
                    )
                }
            }
        }
    }

    private var currentNoteId: Int? = null

    private val _addEditNoteState = MutableStateFlow(AddEditNoteState())
    val addEditNoteState = _addEditNoteState.asStateFlow()

    private val _eventFlow = Channel<AddEditUiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

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
                                color = note.backgroundColor,
                                isPinned = note.isPinned
                            )
                        )

                        _eventFlow.send(AddEditUiEvent.SaveNote)
                    } catch (e: Exception) {
                        _eventFlow.send(
                            AddEditUiEvent.ShowToast(
                                message = e.message ?: "Couldn't save note"
                            )
                        )
                        _eventFlow.send(AddEditUiEvent.SaveNoteFailure)
                    }
                }
            }

            else -> Unit
        }
    }
}