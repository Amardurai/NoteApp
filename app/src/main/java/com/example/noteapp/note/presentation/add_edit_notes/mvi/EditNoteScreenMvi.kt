package com.example.noteapp.note.presentation.add_edit_notes.mvi

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.example.noteapp.note.domain.model.Note
import com.example.noteapp.note.domain.model.NotePreviewParameterProvider
import com.example.noteapp.note.presentation.add_edit_notes.components.EditScreenTopAppBar
import com.example.noteapp.note.presentation.add_edit_notes.components.TransparentTextField
import com.example.noteapp.note.presentation.add_edit_notes.mvvm.LoadingIndicator
import com.example.noteapp.ui.theme.NoteAppTheme
import kotlinx.coroutines.flow.collectLatest


@Composable
fun EditNoteScreenMvi(
    navController: NavController,
    viewModel: MviViewModel = hiltViewModel(),
) {

    val lifecycle = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(viewModel.uiEventFlow) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEventFlow.collectLatest { event ->
                when (event) {
                    EditUiEvent.GoBack -> {
                        navController.popBackStack()
                    }

                    is EditUiEvent.ShowToast -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val noteState = viewModel.editNoteState.collectAsStateWithLifecycle()

    EditNoteScreenMvi(
        noteState = noteState.value,
        onAction = viewModel::onEvent
    )

    BackHandler {
        viewModel.onEvent(EditNoteAction.SaveNote)
    }
}

@Composable
fun EditNoteScreenMvi(
    noteState: EditNoteState,
    onAction: (EditNoteAction) -> Unit,
) {

    Scaffold(
        topBar = {
            EditScreenTopAppBar(
                title = noteState.title,
                isPinned = noteState.isPinned,
                onBackClicked = { onAction(EditNoteAction.SaveNote) },
                pinNote = {
                    onAction(EditNoteAction.TogglePin)
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
                    onTitleChanged = { title -> onAction(EditNoteAction.EnteredTitle(title)) },
                    onContentChanged = { content -> onAction(EditNoteAction.EnteredContent(content)) }
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
    noteState: EditNoteState,
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

        //Title field
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
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(32.dp))

        //Content field
        TransparentTextField(
            text = noteState.content,
            hint = "Start typing your note...",
            modifier = Modifier.fillMaxHeight(),
            onValueChange = onContentChanged,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
            singleLine = false,
            fontSize = 16.sp,
        )
    }
}

@Preview
@Composable
private fun EditNoteScreenMviPreview(
    @PreviewParameter(NotePreviewParameterProvider::class) note: Note
) {
    NoteAppTheme {
        EditNoteScreenMvi(
            noteState = EditNoteState(
                title = note.title,
                content = note.content,
                isPinned = note.isPinned
            ),
            onAction = {}
        )
    }
}



