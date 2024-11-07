package com.example.noteapp.navigation

import android.app.Activity
import android.os.Bundle
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
        composable<Screen.NotesScreen> {
            NotesScreen(navController)
        }

        composable<Screen.AddEditNotesScreen> {

            AddEditNoteScreen(navController)

        }
    }

}