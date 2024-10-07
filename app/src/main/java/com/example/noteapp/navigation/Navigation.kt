package com.example.noteapp.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.noteapp.note.presentation.add_edit_notes.AddEditNoteAction
import com.example.noteapp.note.presentation.add_edit_notes.AddEditNotesViewModel
import com.example.noteapp.util.Screen
import com.example.noteapp.note.presentation.add_edit_notes.components.AddEditNoteScreen
import com.example.noteapp.note.presentation.notes.NotesAction
import com.example.noteapp.note.presentation.notes.NotesViewModel
import com.example.noteapp.note.presentation.notes.components.NotesScreen

@Composable
fun SetupNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.NotesScreen,
    ) {
        composable<Screen.NotesScreen>(
            enterTransition = { fadeIn() },
            popEnterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popExitTransition = { fadeOut() }) {
            val viewModel: NotesViewModel = hiltViewModel()
            val notesState = viewModel.notesFlow.collectAsStateWithLifecycle()

            NotesScreen(
                notesState.value,
                viewModel.eventFlow,
                onAction = { event ->
                    when (event) {
                        NotesAction.AddNoteClick -> navController.navigate(Screen.AddEditNotesScreen(-1))
                        is NotesAction.NoteClicked -> navController.navigate(Screen.AddEditNotesScreen(event.noteId))
                        else-> Unit
                    }
                    viewModel.onEvent(event)
                }
            )
        }

        composable<Screen.AddEditNotesScreen>(
            enterTransition = { scaleIn() },
            popEnterTransition = { scaleIn() },
            exitTransition = { fadeOut() },
            popExitTransition = { fadeOut() }) {

            val viewModel: AddEditNotesViewModel = hiltViewModel()
            val noteState = viewModel.addEditNoteState.collectAsStateWithLifecycle()

            AddEditNoteScreen(
                noteState = noteState.value,
                onUiEvent = viewModel.eventFlow,
                onAction = { event ->
                    when (event) {
                        AddEditNoteAction.OnNoteSaved -> {
                            navController.popBackStack()
                        }
                        else -> Unit
                    }
                    viewModel.onEvent(event)
                })

        }
    }

}