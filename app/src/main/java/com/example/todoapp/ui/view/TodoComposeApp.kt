package com.example.todoapp.ui.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.ui.view.viewmodel.TodoListViewModel

@Composable
fun TodoComposeApp(
    listViewModel: TodoListViewModel,
    onThemeChange: () -> Unit,
    viewModelFactory: ViewModelProvider.Factory
) {
    val navController = rememberNavController()

    NavGraph(
        navController = navController,
        listViewModel = listViewModel,
        onThemeChange = onThemeChange,
        viewModelFactory = viewModelFactory
    )
}