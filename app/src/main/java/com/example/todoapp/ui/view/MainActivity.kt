package com.example.todoapp.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.ui.theme.ToDoAppTheme
import com.example.todoapp.ui.view.viewmodel.TodoListViewModel
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private val listViewModel: TodoListViewModel by viewModels { TodoListViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val initialPreferences = runBlocking {
            listViewModel.getInitialPreferences()
        }

        setContent {
            ToDoAppTheme(darkTheme = initialPreferences.darkTheme) {
                TodoComposeApp(
                    listViewModel = listViewModel,
                    darkTheme = initialPreferences.darkTheme,
                    onThemeChange = { listViewModel.updateDarkTheme(!initialPreferences.darkTheme) }
                )
            }
        }
    }

    @Composable
    fun TodoComposeApp(
        listViewModel: TodoListViewModel,
        darkTheme: Boolean,
        onThemeChange: () -> Unit
    ) {
        val navController = rememberNavController()

        NavGraph(
            navController = navController,
            listViewModel = listViewModel,
            darkTheme = darkTheme,
            onThemeChange = onThemeChange
        )
    }
}
