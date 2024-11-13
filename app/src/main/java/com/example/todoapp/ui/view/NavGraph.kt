package com.example.todoapp.ui.view

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.todoapp.ui.view.viewmodel.EditTodoItemViewModel
import com.example.todoapp.ui.view.viewmodel.TodoListViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.ui.view.screens.EditTodoItem
import com.example.todoapp.ui.view.screens.EditTodoItemScreen
import com.example.todoapp.ui.view.screens.TodoList
import com.example.todoapp.ui.view.screens.TodoListScreen


@Composable
fun NavGraph(
    navController: NavHostController,
    listViewModel: TodoListViewModel,
    darkTheme: Boolean,
    onThemeChange: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = TodoList,
    ) {
        composable<TodoList>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(durationMillis = 300),
                    initialAlpha = 0.999f
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 300),
                    targetAlpha = 0.999f
                )
            },
        ) {
            TodoListScreen(
                viewModel = listViewModel,
                toEditItemScreen = { id ->
                    navController.navigate(EditTodoItem(id)) { launchSingleTop = true }
                },
                darkTheme = darkTheme,
                onThemeChange = onThemeChange
            )
        }
        composable<EditTodoItem>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(
                        300,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(
                        300,
                        easing = FastOutSlowInEasing
                    )
                )
            },
        ) { backStackEntry ->
            val editItemViewModel: EditTodoItemViewModel = viewModel(factory = EditTodoItemViewModel.Factory)
            val editItem: EditTodoItem = backStackEntry.toRoute()
            EditTodoItemScreen(
                itemId = editItem.itemId,
                viewModel = editItemViewModel,
                onClose = {
                    navController.popBackStack()
                }
            )
        }
    }
}