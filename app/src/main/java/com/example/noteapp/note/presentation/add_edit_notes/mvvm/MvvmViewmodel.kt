package com.example.noteapp.note.presentation.add_edit_notes.mvvm

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MvvmViewmodel @Inject constructor(
    private val useCases: UseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var currentNoteId: Int? = null

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _isPinned = MutableStateFlow(false)
    val isPinned = _isPinned.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _noteSaved = Channel<Boolean>()
    val noteSaved = _noteSaved.receiveAsFlow()

    private val _showToast = Channel<String>()
    val showToast = _showToast.receiveAsFlow()


    init {
        loadNoteIfAvailable()
    }


    fun updateTitle(title: String) {
        _title.value = title
    }

    fun updateContent(content: String) {
        _content.value = content
    }

    fun updateIsPinned(isPinned: Boolean) {
        _isPinned.value = isPinned
    }

    fun saveNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                if (title.value.isEmpty() && content.value.isEmpty()) {
                    throw Exception("Empty note discarded.")
                }

                useCases.insertNoteUseCase.invoke(
                    Note(
                        id = currentNoteId,
                        title = title.value.trim(),
                        content = content.value.trim(),
                        timestamp = System.currentTimeMillis(),
                        isPinned = isPinned.value
                    )
                )
            } catch (e: Exception) {
                _showToast.trySend(e.message ?: "Couldn't save note")
            }
            _noteSaved.trySend(true)
        }
    }

    private fun loadNoteIfAvailable() {
        val noteId = savedStateHandle.toRoute<Screen.EditNotesScreen>()

        if (noteId.id != -1) {
            useCases.fetchNoteByIdUseCase.invoke(noteId.id)
                .onEach {
                    _isLoading.value = true
                    delay(2000)
                }
                .onEach { note ->
                    note?.let {
                        currentNoteId = note.id
                        _isPinned.value = note.isPinned
                        _content.value = note.content
                        _title.value = note.title
                        _isLoading.value = false
                    }
                }.catch {
                    _isLoading.value = false
                }.launchIn(viewModelScope)
        }
    }
}