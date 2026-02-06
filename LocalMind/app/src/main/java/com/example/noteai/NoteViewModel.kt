package com.example.noteai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteUiState(
    val notes: List<Note> = emptyList(),
    val selectedNote: Note? = null,
    val aiSuggestion: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllNotes().collect { notes ->
                _uiState.update { it.copy(notes = notes) }
            }
        }
    }

    fun loadNote(noteId: Long) {
        if (noteId == 0L) {
            _uiState.update { it.copy(selectedNote = null, errorMessage = null) }
            return
        }
        viewModelScope.launch {
            val note = repository.getNoteById(noteId)
            _uiState.update { it.copy(selectedNote = note, errorMessage = null) }
        }
    }

    fun saveNote(noteId: Long, title: String, content: String) {
        viewModelScope.launch {
            if (noteId == 0L) {
                repository.addNote(title, content)
            } else {
                val existing = repository.getNoteById(noteId)
                if (existing != null) {
                    repository.updateNote(existing.copy(title = title, content = content))
                }
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun requestSuggestion(prompt: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.generateSuggestion(prompt)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    aiSuggestion = result.getOrDefault(""),
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun clearSuggestion() {
        _uiState.update { it.copy(aiSuggestion = "") }
    }

    class Factory(private val repository: NoteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
                return NoteViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
