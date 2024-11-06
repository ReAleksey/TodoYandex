package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.ui.theme.ToDoAppTheme
import com.example.todoapp.viewmodel.EditTodoItemViewModel
import com.example.todoapp.viewmodel.TodoListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            var darkTheme by remember { mutableStateOf(systemDarkTheme) }

            ToDoAppTheme(darkTheme = darkTheme) {
                TodoComposeApp(
                    darkTheme = darkTheme,
                    onThemeChange = { darkTheme = !darkTheme }
                )
            }
        }
    }

    @Composable
    fun TodoComposeApp(darkTheme: Boolean, onThemeChange: () -> Unit) {
        val navController = rememberNavController()
        val listViewModel: TodoListViewModel by viewModels { TodoListViewModel.Factory }
        val editItemViewModel: EditTodoItemViewModel by viewModels { EditTodoItemViewModel.Factory }

        NavGraph(
            navController = navController,
            listViewModel = listViewModel,
            editItemViewModel = editItemViewModel,
            darkTheme = darkTheme,
            onThemeChange = onThemeChange
        )
    }
}
