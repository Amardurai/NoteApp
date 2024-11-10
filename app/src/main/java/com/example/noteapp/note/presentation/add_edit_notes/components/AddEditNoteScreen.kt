package com.example.noteapp.note.presentation.add_edit_notes.components

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.example.noteapp.utils.shareNote
import com.example.noteapp.note.presentation.add_edit_notes.AddEditNoteAction
import com.example.noteapp.note.presentation.add_edit_notes.AddEditNoteState
import com.example.noteapp.note.presentation.add_edit_notes.AddEditNotesViewModel
import com.example.noteapp.note.presentation.add_edit_notes.AddEditUiEvent
import com.example.noteapp.ui.theme.NoteAppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf


@Composable
fun AddEditNoteScreen(
    navController: NavController,
    viewModel: AddEditNotesViewModel = hiltViewModel(),
) {

    val lifecycle = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(viewModel.eventFlow) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {

                    AddEditUiEvent.SaveNote -> {
                        navController.popBackStack()
                    }

                    is AddEditUiEvent.ShowToast -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    val noteState = viewModel.addEditNoteState.collectAsStateWithLifecycle()

    AddEditNoteScreen(
        noteState = noteState.value,
        onAction = viewModel::onEvent
    )

    BackHandler {
        viewModel.onEvent(AddEditNoteAction.SaveNote)
    }
}

@Composable
fun AddEditNoteScreen(
    noteState: AddEditNoteState,
    onAction: (AddEditNoteAction) -> Unit,
) {

    val context = LocalContext.current

    Scaffold(
        topBar = {
            AddEditScreenTopAppBar(
                noteState = noteState,
                onBackClicked = { onAction(AddEditNoteAction.SaveNote) },
                pinNote = {
                    onAction(AddEditNoteAction.PinNote(value = !noteState.isPinned))
                },
                shareNote = {
                    val noteContent = prepareNoteContentForSharing(noteState)
                    context.shareNote(noteContent)
                },
            )
        },
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            if (noteState.isLoading) {
                LoadingIndicator()
            } else {
                NoteEditor(
                    noteState = noteState,
                    onTitleChanged = { title -> onAction(AddEditNoteAction.EnteredTitle(title)) },
                    onContentChanged = { content -> onAction(AddEditNoteAction.EnteredContent(content)) }
                )
            }
        }
    }

}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun NoteEditor(
    noteState: AddEditNoteState,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        TransparentTextField(
            text = noteState.title,
            hint = "Type your title...",
            onValueChange = onTitleChanged,
            textStyle = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            singleLine = true,
            fontSize = 25.sp,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        )

        Spacer(modifier = Modifier.height(32.dp))

        TransparentTextField(
            text = noteState.content,
            hint = "Start typing your note...",
            modifier = Modifier.fillMaxHeight(),
            onValueChange = onContentChanged,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
            singleLine = false,
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun AddEditNoteScreenPreview() {
    NoteAppTheme {
        AddEditNoteScreen(
            noteState = AddEditNoteState(
                title = "Kotlin Best Practices",
                content = "1. Utilize extension functions to enhance readability and maintainability in your code.\n\n" +
                        "2. Make use of scope functions and control structures to reduce code redundancy",
                isPinned = true
            ),
            onAction = {}
        )
    }
}


private fun prepareNoteContentForSharing(note: AddEditNoteState): String {
    return "${note.title}\n\n${note.content}"
}



