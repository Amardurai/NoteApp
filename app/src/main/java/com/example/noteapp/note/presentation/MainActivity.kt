package com.example.noteapp.note.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import com.example.noteapp.navigation.SetupNavigation
import com.example.noteapp.ui.theme.NoteAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteAppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                      val navController = rememberNavController()
                      SetupNavigation(navController)
//                    RecompositionTest()

                }
            }
        }
    }
}

data class NamesState(
    val names: List<String> = emptyList()
)

class NamesViewModel :ViewModel(){
    private val _state = MutableStateFlow(NamesState())
    val state = _state.asStateFlow()

    fun addName(){
        _state.value = _state.value.copy(
            names = _state.value.names + "New Name"
        )
    }

    fun handleNameClick(){
        println("Amardurai")
    }
}

@Composable
fun RecompositionTest() {
    val viewModel = remember { NamesViewModel() }
    val state by viewModel.state.collectAsState()

    NameColumnWithButton(
        names = state.names,
        onButtonClick = { viewModel.addName() },
        onNameClick = { viewModel.handleNameClick() },
    )
}

@Composable
fun NameColumnWithButton(
    names: List<String>,
    onButtonClick: () -> Unit,
    onNameClick: () -> Unit,
) {
    Column {
        names.forEach {
            CompositionTrackingName(name = it, onClick = onNameClick)
        }
        Button(onClick = onButtonClick) { Text("Add a Name") }
    }
}

@Composable
fun CompositionTrackingName(name: String, onClick: () -> Unit) {
    Log.e("*******COMPOSED", name)
    Text(name, modifier = Modifier.clickable(onClick = onClick))
}