package com.example.noteapp.note.presentation.notes.components

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.example.noteapp.R
import com.example.noteapp.note.domain.model.Note
import com.example.noteapp.note.presentation.notes.NotesAction
import com.example.noteapp.note.presentation.notes.NotesState
import com.example.noteapp.note.presentation.notes.NotesUiEvent
import com.example.noteapp.note.presentation.notes.NotesViewModel
import com.example.noteapp.ui.theme.NoteAppTheme
import com.example.noteapp.util.Screen
import kotlinx.coroutines.flow.collectLatest


@Composable
fun NotesScreen(navController: NavController, viewModel: NotesViewModel = hiltViewModel()) {

    val notesState = viewModel.notesFlow.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.eventFlow) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventFlow.collectLatest { event ->
                if (event is NotesUiEvent.ShowSnackBar) {
                    val result = snackBarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(NotesAction.RestoreNote)
                    }
                }
            }
        }
    }


    NotesScreen(
        noteState = notesState.value,
        onAction = { event ->
            when (event) {
                NotesAction.AddNoteClick -> navController.navigate(Screen.AddEditNotesScreen(-1))
                is NotesAction.NoteClicked -> navController.navigate(Screen.AddEditNotesScreen(event.noteId))
                else -> Unit
            }
            viewModel.onEvent(event)
        },
        snackBarHostState = snackBarHostState
    )

}


@Composable
fun NotesScreen(
    noteState: NotesState,
    onAction: (NotesAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            NotesScreenTopAppBar()
        },
        floatingActionButton = {
            NotesScreenFab(onAddNote = {
                onAction(NotesAction.AddNoteClick)
            })
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { innerPadding ->
        NotesContent(
            noteState = noteState,
            innerPadding = innerPadding,
            onDeleteNote = { note ->
                onAction(NotesAction.DeleteNote(note))
            },
            onNoteClicked = { noteId ->
                onAction(NotesAction.NoteClicked(noteId))
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreenTopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(text = "Notes", style = MaterialTheme.typography.headlineSmall)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
    )
}


@Composable
fun NotesContent(
    noteState: NotesState,
    innerPadding: PaddingValues,
    onDeleteNote: (Note) -> Unit,
    onNoteClicked: (Int) -> Unit,
) {
    if (noteState.notes.isEmpty()) {
        EmptyScreen(stringRes = R.string.empty_notes_screen_error_msg)
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            items(
                items = noteState.notes,
                key = { it.id!! }
            ) { note ->
                SwipeBox(onDelete = { onDeleteNote(note) }) {
                    NoteCard(
                        modifier = Modifier.animateItem(),
                        note = note,
                        onClick = { onNoteClicked(note.id ?: 0) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBox(
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(confirmValueChange = { dismissValue ->
        if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
        }
        return@rememberSwipeToDismissBoxState true
    })

    val (icon, alignment, color) = when (swipeState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Triple(
            Icons.Outlined.Delete,
            Alignment.CenterEnd,
            MaterialTheme.colorScheme.errorContainer
        )

        SwipeToDismissBoxValue.StartToEnd -> Triple(
            Icons.Outlined.Block,
            Alignment.CenterStart,
            MaterialTheme.colorScheme.background
        )

        SwipeToDismissBoxValue.Settled -> Triple(
            Icons.Outlined.Delete,
            Alignment.CenterEnd,
            MaterialTheme.colorScheme.background
        )
    }

    SwipeToDismissBox(
        modifier = modifier
            .padding(12.dp)
            .animateContentSize()
            .clip(shape = RoundedCornerShape(16.dp)),
        state = swipeState,
        backgroundContent = {
            Box(
                contentAlignment = alignment,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
            ) {
                Icon(
                    modifier = Modifier.minimumInteractiveComponentSize(),
                    imageVector = icon, contentDescription = null
                )
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        content()
    }
}

@Composable
fun NotesScreenFab(onAddNote: () -> Unit) {
    FloatingActionButton(
        shape = CircleShape,
        modifier = Modifier.padding(16.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        onClick = onAddNote
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Note",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun NotesScreenPreview() {
    NoteAppTheme {
        NotesScreen(
            noteState = NotesState(
                notes = (1..10).map {
                    Note(
                        id = 1,
                        title = "Jetpack Compose",
                        content = "Compose is a modern toolkit for building native Android UI. It simplifies and accelerates UI development on Android",
                        timestamp = 100,
                        isPinned = true,
                    )
                }
            ),
            onAction = { },
            snackBarHostState = SnackbarHostState()
        )
    }
}
