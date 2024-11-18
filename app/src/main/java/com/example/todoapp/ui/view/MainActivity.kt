package com.example.todoapp.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.data.repository.UserPreferences
import com.example.todoapp.ui.theme.ToDoAppTheme
import com.example.todoapp.ui.view.viewmodel.TodoListViewModel

class MainActivity : ComponentActivity() {

    private val listViewModel: TodoListViewModel by viewModels { TodoListViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val userPreferences by listViewModel.userPreferencesFlow.collectAsStateWithLifecycle(
                initialValue = UserPreferences(darkTheme = false, showCompleted = true)
            )

            ToDoAppTheme(darkTheme = userPreferences.darkTheme) {
                TodoComposeApp(
                    listViewModel = listViewModel,
                    onThemeChange = { listViewModel.updateDarkTheme(!userPreferences.darkTheme) }
                )
            }
        }
    }
}
