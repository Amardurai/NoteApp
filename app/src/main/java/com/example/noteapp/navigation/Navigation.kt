package com.example.noteapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.noteapp.note.presentation.add_edit_notes.mvi.EditNoteScreenMvi
import com.example.noteapp.note.presentation.add_edit_notes.mvvm.EditNoteScreenMvvm
import com.example.noteapp.note.presentation.notes.components.NotesScreen
import com.example.noteapp.util.Screen

@Composable
fun SetupNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.NotesScreen,
    ) {
        composable<Screen.NotesScreen> {
            NotesScreen(navController)
        }

        composable<Screen.EditNotesScreen> {
            EditNoteScreenMvi(navController)
        }
    }

}