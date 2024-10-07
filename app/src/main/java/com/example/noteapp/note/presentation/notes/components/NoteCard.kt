package com.example.noteapp.note.presentation.notes.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.noteapp.R
import com.example.noteapp.note.domain.model.Note
import com.example.noteapp.ui.theme.NoteAppTheme

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    cornerRadius: Dp = 10.dp,
    elevation: Dp = 4.dp,
    onClick: () -> Unit
) {
    val defaultBackgroundColor = MaterialTheme.colorScheme.background
    val defaultOnBackgroundColor = MaterialTheme.colorScheme.onBackground
    val noteTextColor = defaultOnBackgroundColor
    val noteBackgroundColor = if (note.color != -1) Color(note.color) else defaultBackgroundColor

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(elevation),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = noteBackgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title and Lock Icon Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = noteTextColor,
                    modifier = Modifier.weight(1f) // Title takes up remaining space
                )

            }

            Spacer(modifier = Modifier.height(8.dp))

            if (note.content.isNotEmpty()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = noteTextColor
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun NoteItemPreview() {
    NoteAppTheme {
        NoteCard(
            note = Note(
                id = null,
                title = "Grocery List",
                content = """
                Eggs
                Milk
                Bread
                Rice
                Bananas
            """.trimIndent(),
                timestamp = 100,
                color = R.color.purple_200,
                isPinned = false,
            ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun NoteItemLightPreview() {
    NoteCard(
        note = Note(
            id = null,
            title = "My Title",
            content = """
                Hello, I am android developer
                working 
            """.trimIndent(),
            timestamp = 100,
            color = R.color.teal_200,
            isPinned = true,
        ),
        onClick = {},
    )
}