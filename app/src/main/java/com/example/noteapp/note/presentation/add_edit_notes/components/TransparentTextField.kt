package com.example.noteapp.note.presentation.add_edit_notes.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit


@Composable
fun TransparentTextField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    fontSize: TextUnit,
    keyboardOptions: KeyboardOptions = KeyboardOptions()
) {


    BasicTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = text,
        onValueChange = onValueChange,
        singleLine = singleLine,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
        textStyle = textStyle.copy(
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.onBackground
        ), keyboardOptions = keyboardOptions
    ) { innerTextField ->
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (text.isBlank()) {
                Text(
                    modifier = Modifier.alpha(alpha = 0.5F),
                    text = hint,
                    style = textStyle,
                    fontSize = fontSize,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        innerTextField()
    }
}
