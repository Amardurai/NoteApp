package com.example.noteapp.note.presentation.add_edit_notes.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import com.example.noteapp.note.presentation.add_edit_notes.AddEditNoteState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreenTopAppBar(
    noteState: AddEditNoteState,
    onBackClicked: () -> Unit,
    pinNote: () -> Unit,
    shareNote: () -> Unit,
) {

    val scope = rememberCoroutineScope()

    TopAppBar(
        title = { Text("") },
        navigationIcon = {
            IconButton(onBackClicked) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back arrow",
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        ),
        actions = {
            IconButton(onClick = {
                scope.launch {
                    pinNote()
                }
            }
            ) {
                Icon(
                    imageVector = if (noteState.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                    contentDescription = "Pin note",
                )
            }

            if (noteState.title.isNotEmpty()) {
                IconButton(onClick = {
                    scope.launch {
                        shareNote()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share note",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    )
}
