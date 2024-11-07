package com.example.noteapp.note.presentation.notes.components

import androidx.compose.ui.test.junit4.createComposeRule
import com.example.noteapp.note.presentation.notes.NotesState
import kotlinx.coroutines.flow.flow
import org.junit.Rule
import org.junit.Test

class NotesScreenKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun notesScreen() {
        composeTestRule.setContent {

            NotesScreen(
                noteState = NotesState(),
                flow { },
                {}
            )
        }
    }
}