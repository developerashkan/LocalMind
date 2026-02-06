package com.example.noteai.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.ArrowBack
import androidx.compose.material3.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteai.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    noteId: Long,
    viewModel: NoteViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var prompt by rememberSaveable { mutableStateOf("") }
    var suggestionVisible by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    LaunchedEffect(uiState.selectedNote?.id) {
        val note = uiState.selectedNote
        if (note != null) {
            title = note.title
            content = note.content
        } else if (noteId == 0L) {
            title = ""
            content = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (noteId == 0L) "New Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val selectedNote = uiState.selectedNote
                    if (selectedNote != null) {
                        IconButton(onClick = {
                            viewModel.deleteNote(selectedNote)
                            onBack()
                        }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )
            Button(
                onClick = {
                    viewModel.saveNote(noteId, title, content)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Ask Ollama for a suggestion", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Prompt") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        suggestionVisible = true
                        viewModel.requestSuggestion(prompt)
                    },
                    enabled = prompt.isNotBlank()
                ) {
                    Text(text = "Generate")
                }
                Button(onClick = {
                    suggestionVisible = false
                    viewModel.clearSuggestion()
                }) {
                    Text(text = "Clear")
                }
            }

            if (uiState.isLoading && suggestionVisible) {
                CircularProgressIndicator()
            }

            if (uiState.errorMessage != null && suggestionVisible) {
                Text(
                    text = "Error: ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (uiState.aiSuggestion.isNotBlank() && suggestionVisible) {
                Text(
                    text = uiState.aiSuggestion,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
