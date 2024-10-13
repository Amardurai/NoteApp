package com.example.noteapp.note.presentation.add_edit_notes.components

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.noteapp.utils.shareNote
import com.example.noteapp.note.presentation.add_edit_notes.AddEditNoteAction
import com.example.noteapp.note.presentation.add_edit_notes.AddEditNoteState
import com.example.noteapp.note.presentation.add_edit_notes.AddEditUiEvent
import com.example.noteapp.ui.theme.NoteAppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf


@Composable
fun AddEditNoteScreen(
    noteState: AddEditNoteState,
    onUiEvent: Flow<AddEditUiEvent>,
    onAction: (AddEditNoteAction) -> Unit,
) {
    val backgroundColor = MaterialTheme.colorScheme.background

    val context = LocalContext.current

    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current

    LaunchedEffect(onUiEvent) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            onUiEvent.collectLatest { event ->
                when (event) {
                    AddEditUiEvent.SaveNote -> {
                        onAction(AddEditNoteAction.OnNoteSaved)
                    }

                    AddEditUiEvent.SaveNoteFailure -> {
                        onAction(AddEditNoteAction.OnNoteSaved)
                    }

                    is AddEditUiEvent.ShowToast -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    BackHandler {
        onAction(AddEditNoteAction.SaveNote)
    }

    Scaffold(
        topBar = {
            AddEditScreenTopAppBar(
                onBackClicked = { onAction(AddEditNoteAction.SaveNote) },
                pinNote = {
                    onAction(AddEditNoteAction.PinNote(value = !noteState.isPinned))
                },
                shareNote = {
                    val noteContent = prepareNoteContentForSharing(noteState)
                    context.shareNote(noteContent)
                },
                isPinned = noteState.isPinned,
                showShareNoteIcon = noteState.title.isNotEmpty()
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            TransparentTextField(
                text = noteState.title,
                hint = "Type your title...",
                onValueChange = {
                    onAction(AddEditNoteAction.EnteredTitle(it))
                },
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                singleLine = true,
                fontSize = 25.sp,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )


            Spacer(modifier = Modifier.height(32.dp))

            TransparentTextField(
                text = noteState.content,
                hint = "Start typing your note...",
                modifier = Modifier.fillMaxHeight(),
                onValueChange = {
                    onAction(AddEditNoteAction.EnteredContent(it))
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                singleLine = false,
                fontSize = 16.sp
            )
        }
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
            onUiEvent = flowOf(),
            onAction = {}
        )
    }
}


private fun prepareNoteContentForSharing(note: AddEditNoteState): String {
    return "${note.title}\n\n${note.content}"
}



