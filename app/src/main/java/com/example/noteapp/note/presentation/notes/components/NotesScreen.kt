package com.example.noteapp.note.presentation.notes.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteapp.note.presentation.notes.NotesAction
import com.example.noteapp.note.presentation.notes.NotesUiEvent
import com.example.noteapp.note.presentation.notes.SearchBarState
import com.example.noteapp.R
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.noteapp.note.presentation.notes.NotesState
import kotlinx.coroutines.flow.Flow

@Composable
fun NotesScreen(
    noteState: NotesState,
    uiEvent: Flow<NotesUiEvent>,
    onAction: (NotesAction) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }

    val snackBarHostState = remember { SnackbarHostState() }

    fun clearSearchText() {
        searchText = ""
    }

    fun isSearchBarOpened() = (noteState.searchBarState == SearchBarState.OPENED)

    BackHandler {
        if (isSearchBarOpened()) {
            onAction(NotesAction.CloseSearchBar)
        }
    }
    val lifecycle = LocalLifecycleOwner.current

    LaunchedEffect(uiEvent) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            uiEvent.collectLatest { event ->
                when (event) {
                    is NotesUiEvent.ShowSnackBar -> {
                        val result = snackBarHostState.showSnackbar(
                            message = event.message,
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            onAction(NotesAction.RestoreNote)
                        }
                    }
                }
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            AnimatedVisibility(
                visible = isSearchBarOpened(),
                enter = expandHorizontally(expandFrom = Alignment.Start),
                exit = shrinkHorizontally()
            ) {
                SearchAppBar(text = searchText, onTextChanged = { text ->
                    searchText = text

                    onAction(NotesAction.SearchNotes(text))

                    if (text.isEmpty()) {
                        onAction(NotesAction.GetAllNotes)
                    }
                }, onClearClicked = {
                    clearSearchText()
                }, onBackClicked = {
                    clearSearchText()
                    onAction(NotesAction.CloseSearchBar)
                    onAction(NotesAction.GetAllNotes)
                })

            }

            if (noteState.searchBarState == SearchBarState.CLOSED) {
                NotesScreenTopAppBar(onSearchTriggered = { onAction(NotesAction.OpenSearchBar) })
            }

        },
        floatingActionButton = {
            NotesScreenFab(onAddNote = {
                onAction(NotesAction.CloseSearchBar)
                onAction(NotesAction.AddNoteClick)
            })
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { innerPadding ->

        if (isSearchBarOpened() && noteState.notes.isEmpty()) {
            EmptyScreen(
                stringRes = R.string.no_matching_note_found_error_msg
            )
        } else if (noteState.notes.isEmpty()) {
            EmptyScreen(
                stringRes = R.string.empty_notes_screen_error_msg
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(all = 8.dp),
        ) {
            items(
                items = noteState.notes,
                key = { it.id!! },
            ) { note ->

                SwipeBox(onDelete = {
                    onAction(NotesAction.DeleteNote(note))
                }) {
                    NoteCard(
                        modifier = Modifier.animateItem(),
                        note = note,
                        onClick = {
                            onAction(NotesAction.NoteClicked(note.id ?: 0))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBox(
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

    lateinit var icon: ImageVector
    lateinit var alignment: Alignment
    var color: Color = MaterialTheme.colorScheme.background

    when (swipeState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> {
            icon = Icons.Outlined.Delete
            alignment = Alignment.CenterEnd
            color = MaterialTheme.colorScheme.errorContainer
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            icon = Icons.Outlined.Block
            alignment = Alignment.CenterStart
            color = MaterialTheme.colorScheme.background
        }


        SwipeToDismissBoxValue.Settled -> {
            icon = Icons.Outlined.Delete
            alignment = Alignment.CenterEnd
            color = MaterialTheme.colorScheme.background
        }
    }

    SwipeToDismissBox(
        modifier = modifier
            .padding(12.dp)
            .animateContentSize(),
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
        }, enableDismissFromStartToEnd = false
    ) {
        content()
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreenTopAppBar(
    onSearchTriggered: () -> Unit
) {

    CenterAlignedTopAppBar(title = {
        Text(
            text = "Notes", style = MaterialTheme.typography.headlineSmall
        )
    }, colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onBackground
    ), actions = {
        IconButton(onClick = { onSearchTriggered() }) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    })
}

@Composable
fun NotesScreenFab(onAddNote: () -> Unit) {
    FloatingActionButton(
        shape = CircleShape,
        modifier = Modifier.padding(all = 16.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        onClick = { onAddNote() },
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Note",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}


