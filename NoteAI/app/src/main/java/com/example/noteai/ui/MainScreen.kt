package com.example.noteai.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.noteai.Note
import com.example.noteai.NoteViewModel

@Composable
fun MainScreen(
    viewModel: NoteViewModel,
    onAddNote: () -> Unit,
    onOpenNote: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add note")
            }
        }
    ) { padding ->
        if (uiState.notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text(
                    text = "No notes yet. Tap + to create one.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.notes) { note ->
                    NoteCard(note = note, onClick = { onOpenNote(note.id) })
                }
            }
        }
    }
}

@Composable
private fun NoteCard(note: Note, onClick: () -> Unit) {
    Card(modifier = Modifier.clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = note.title.ifBlank { "Untitled" }, style = MaterialTheme.typography.titleMedium)
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
