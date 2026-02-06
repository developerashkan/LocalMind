package com.example.noteai

import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao,
    private val ollamaHelper: OllamaHelper
) {
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAll()

    suspend fun getNoteById(noteId: Long): Note? = noteDao.getById(noteId)

    suspend fun addNote(title: String, content: String): Long {
        val note = Note(
            title = title,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        return noteDao.insert(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.update(note.copy(timestamp = System.currentTimeMillis()))
    }

    suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    suspend fun generateSuggestion(prompt: String): Result<String> {
        return ollamaHelper.generateSuggestion(prompt)
    }
}
