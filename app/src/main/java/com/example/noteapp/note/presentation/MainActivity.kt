package com.example.noteapp.note.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.noteapp.navigation.SetupNavigation
import com.example.noteapp.ui.theme.NoteAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteAppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
/*                    var name by remember { mutableStateOf("Android") }
                    val context = LocalContext.current
                    Column {
                        Text("Hello World")
                        TextField(
                            value = name,
                            onValueChange = { name = it }
                        )
                        TextAndButton(
                            name,
                            onClick = {
                                Toast.makeText(context, "Hello $name", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }*/

                      val navController = rememberNavController()
                      SetupNavigation(navController)
                }
            }
        }
    }
}

@Composable
fun TextAndButton(
    name: String,
    onClick: () -> Unit
) {
    Column {
        Text(text = "Hello $name!")

        Button(onClick = onClick) {
            Text("Click Me")
        }
    }
}