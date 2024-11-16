package com.example.noteapp.note.presentation.add_edit_notes.mvvm

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.example.noteapp.utils.shareNote
import com.example.noteapp.note.presentation.add_edit_notes.components.EditScreenTopAppBar
import com.example.noteapp.note.presentation.add_edit_notes.components.TransparentTextField
import com.example.noteapp.ui.theme.NoteAppTheme
import kotlinx.coroutines.flow.collectLatest


@Composable
fun EditNoteScreenMvvm(
    navController: NavController,
    viewModel: MvvmViewmodel = hiltViewModel(),
) {

    val lifecycle = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.noteSaved.collectLatest {
                navController.navigateUp()
            }
            viewModel.showToast.collectLatest {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val title = viewModel.title.collectAsState()
    val content = viewModel.content.collectAsState()
    val isPinned = viewModel.isPinned.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    EditNoteScreenMvvm(
        title = title.value,
        content = content.value,
        isPinned = isPinned.value,
        isLoading = isLoading.value,
        onTitleChange = viewModel::updateTitle,
        onContentChange = viewModel::updateContent,
        onPinChange = viewModel::updateIsPinned,
        toSaveNote = viewModel::saveNotes
    )

    BackHandler {
        viewModel.saveNotes()
    }
}

@Composable
fun EditNoteScreenMvvm(
    title: String,
    content: String,
    isPinned: Boolean,
    isLoading: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onPinChange: (Boolean) -> Unit,
    toSaveNote: () -> Unit,
) {

    val context = LocalContext.current

    Scaffold(
        topBar = {
            EditScreenTopAppBar(
                title = title,
                isPinned = isPinned,
                onBackClicked = toSaveNote,
                pinNote = { onPinChange(!isPinned) },
            )
        },
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            if (isLoading) {
                LoadingIndicator()
            } else {
                NoteEditor(
                    title = title,
                    content = content,
                    onTitleChanged = { title -> onTitleChange(title) },
                    onContentChanged = { content -> onContentChange(content) }
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
    title: String,
    content: String,
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
            text = title,
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
            text = content,
            hint = "Start typing your note...",
            modifier = Modifier.fillMaxHeight(),
            onValueChange = onContentChanged,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
            singleLine = false,
            fontSize = 16.sp
        )
    }
}

@Composable
@PreviewLightDark
private fun EditNoteScreenMvvmPreview() {
    NoteAppTheme {
        EditNoteScreenMvvm(
            title = "Title",
            content = "Content",
            isPinned = false,
            isLoading = false,
            onTitleChange = {},
            onContentChange = {},
            onPinChange = {},
            toSaveNote = {}
        )
    }
}


private fun prepareNoteContentForSharing(title: String, content: String): String {
    return "${title}\n\n${content}"
}



