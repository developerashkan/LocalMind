package com.example.noteai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.noteai.ui.DetailScreen
import com.example.noteai.ui.MainScreen

class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels {
        val database = NoteDatabase.getInstance(this)
        val repository = NoteRepository(database.noteDao(), OllamaHelper())
        NoteViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteAiApp(viewModel)
        }
    }
}

@Composable
fun NoteAiApp(viewModel: NoteViewModel) {
    val navController = rememberNavController()

    MaterialTheme {
        Surface(color = Color(0xFFF7F7F7)) {
            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainScreen(
                        viewModel = viewModel,
                        onAddNote = { navController.navigate("detail/0") },
                        onOpenNote = { noteId -> navController.navigate("detail/$noteId") }
                    )
                }
                composable("detail/{noteId}") { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId")?.toLongOrNull() ?: 0L
                    DetailScreen(
                        noteId = noteId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
