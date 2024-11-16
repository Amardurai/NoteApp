package com.example.noteapp.note.presentation.add_edit_notes.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MviViewModel @Inject constructor(
    private val useCases: UseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var currentNoteId: Int? = null

    private val _editNoteState = MutableStateFlow(EditNoteState())
    val editNoteState = _editNoteState.asStateFlow()

    private val _uiEventFlow = Channel<EditUiEvent>()
    val uiEventFlow = _uiEventFlow.receiveAsFlow()

    init {
        loadNoteIfAvailable()
    }

    fun onEvent(event: EditNoteAction) {
        when (event) {
            is EditNoteAction.EnteredTitle -> {
                _editNoteState.update { it.copy(title = event.value) }
            }

            is EditNoteAction.EnteredContent -> {
                _editNoteState.update { it.copy(content = event.value) }
            }

            is EditNoteAction.TogglePin -> {
                _editNoteState.update { it.copy(isPinned = !it.isPinned) }
            }

            is EditNoteAction.SaveNote -> saveNotes()

        }
    }

    private fun saveNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val note = _editNoteState.value

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
                _uiEventFlow.send(
                    EditUiEvent.ShowToast(
                        message = e.message ?: "Couldn't save note"
                    )
                )
            }
            _uiEventFlow.send(EditUiEvent.GoBack)
        }
    }

    private fun loadNoteIfAvailable() {
        val noteId = savedStateHandle.toRoute<Screen.EditNotesScreen>()

        if (noteId.id != -1) {
            useCases.fetchNoteByIdUseCase.invoke(noteId.id)
                .onEach {
                    _editNoteState.update { it.copy(isLoading = true) }
                    delay(2000)
                }
                .onEach { note ->
                    note?.let {
                        currentNoteId = note.id
                        _editNoteState.value = _editNoteState.value.copy(
                            title = note.title,
                            content = note.content,
                            isPinned = note.isPinned,
                            isLoading = false
                        )
                    }
                }.catch {
                    _editNoteState.update { it.copy(isLoading = false) }
                }.launchIn(viewModelScope)
        }
    }
}