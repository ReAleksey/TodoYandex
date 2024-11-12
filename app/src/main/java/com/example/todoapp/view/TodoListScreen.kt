package com.example.todoapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.example.todoapp.view.items.BoxWithShadows
import com.example.todoapp.R
import com.example.todoapp.view.items.Sides
import com.example.todoapp.view.items.TodoItemRow
import com.example.todoapp.view.items.TodoListToolbar
import com.example.todoapp.viewmodel.TodoListUiState
import com.example.todoapp.viewmodel.TodoListViewModel
import kotlinx.serialization.Serializable
import androidx.compose.ui.tooling.preview.Preview
import com.example.todoapp.model.TodoImportance
import com.example.todoapp.model.TodoItem
import com.example.todoapp.model.TodoItemRepository
import com.example.todoapp.model.TodoItemsRepositoryImpl
import com.example.todoapp.ui.theme.ToDoAppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.util.Date
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.LocalContext


@Serializable
data object TodoList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel,
    toEditItemScreen: (itemId: String?) -> Unit,
    darkTheme: Boolean,
    onThemeChange: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is TodoListUiState.Error) {
            snackbarHostState.showSnackbar("Что-то пошло не так")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TodoListToolbar(
                scrollBehavior = scrollBehavior,
                topPadding = 0.dp,
                darkTheme = darkTheme,
                onThemeChange = onThemeChange,
                doneCount = when (val state = uiState) {
                    is TodoListUiState.Loaded -> state.doneCount
                    else -> 0
                },
                filterState = when (val state = uiState) {
                    is TodoListUiState.Loaded -> state.filterState
                    else -> TodoListUiState.FilterState.ALL
                },
                onFilterChange = { viewModel.onFilterChange(it) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { toEditItemScreen(null) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add))
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is TodoListUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TodoListUiState.Loaded -> {
                val state = uiState as TodoListUiState.Loaded
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = paddingValues.calculateTopPadding()
                        ),
                    state = lazyListState,
                    userScrollEnabled = true
                ) {
                    items(state.items) { item ->
                        TodoItemRow(
                            item = item,
                            onChecked = { isChecked ->
                                viewModel.onChecked(item, isChecked)
                            },
                            onDeleted = {
                                viewModel.delete(item)
                            },
                            onInfoClicked = {
                                toEditItemScreen(item.id)
                            }
                        )
                    }
                }
            }
            is TodoListUiState.Error -> {
                val items = viewModel.getLastKnownItems()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = paddingValues.calculateTopPadding()
                        ),
                    state = lazyListState,
                    userScrollEnabled = true
                ) {
                    items(items) { item ->
                        TodoItemRow(
                            item = item,
                            onChecked = { isChecked ->
                                viewModel.onChecked(item, isChecked)
                            },
                            onDeleted = {
                                viewModel.delete(item)
                            },
                            onInfoClicked = {
                                toEditItemScreen(item.id)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun TodoListScreenLightPreview() {
    ToDoAppTheme(darkTheme = false) {
        TodoListScreen(
            viewModel = previewViewModel(),
            toEditItemScreen = {},
            darkTheme = false,
            onThemeChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoListScreenDarkPreview() {
    ToDoAppTheme(darkTheme = true) {
        TodoListScreen(
            viewModel = previewViewModel(),
            toEditItemScreen = {},
            darkTheme = true,
            onThemeChange = {}
        )
    }
}

@Composable
private fun previewViewModel(): TodoListViewModel {
    val context = LocalContext.current
    return TodoListViewModel(
        todoItemRepository = TodoItemsRepositoryImpl(context)
    )
}