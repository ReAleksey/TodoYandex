package com.example.todoapp.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.data.repository.UserPreferences
import com.example.todoapp.ui.theme.ToDoAppTheme
import com.example.todoapp.ui.view.viewmodel.TodoListViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val listViewModel: TodoListViewModel by viewModels { viewModelFactory }
    private lateinit var initialPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TodoApp).appComponent
            .activityComponent()
            .create()
            .inject(this)

        super.onCreate(savedInstanceState)

        initialPreferences = runBlocking {
            listViewModel.getInitialPreferences()
        }

        setContent {
            var darkTheme by remember { mutableStateOf(initialPreferences.darkTheme) }

            ToDoAppTheme(darkTheme = darkTheme) {
                TodoComposeApp(
                    listViewModel = listViewModel,
                    onThemeChange = {
                        darkTheme = !darkTheme
                        listViewModel.updateDarkTheme(darkTheme)
                    },
                    viewModelFactory = viewModelFactory
                )
            }
        }
    }
}
