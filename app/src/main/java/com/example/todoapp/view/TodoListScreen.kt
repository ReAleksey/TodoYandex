package com.example.todoapp.view

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.model.TodoImportance
import com.example.todoapp.model.TodoItem
import com.example.todoapp.model.TodoItemRepository
import com.example.todoapp.model.TodoItemsRepositoryImpl
import com.example.todoapp.ui.theme.ToDoAppTheme
import com.example.todoapp.view.items.TodoItemRow
import com.example.todoapp.view.items.TodoListToolbar
import com.example.todoapp.viewmodel.TodoListEvent
import com.example.todoapp.viewmodel.TodoListUiState
import com.example.todoapp.viewmodel.TodoListViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.Serializable
import java.util.Date


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
    var refreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = refreshing)


    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is TodoListEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            refreshing = true
            viewModel.retryFetchingData()
            refreshing = false
        }
    ) {
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
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
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
                        items(state.items, key = { it.id }) { item ->
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
                    val message = (uiState as TodoListUiState.Error).message
                    ErrorScreen(
                        message = message,
                        onRetry = { viewModel.retryFetchingData() },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(text = "Retry")
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
    val mockRepository = object : TodoItemRepository {
        private val items = listOf(
            TodoItem(
                id = "1",
                text = "Todo 1",
                importance = TodoImportance.DEFAULT,
                isCompleted = false,
                createdAt = Date(),
                modifiedAt = Date()
            ),
            TodoItem(
                id = "2",
                text = "Todo 2",
                importance = TodoImportance.HIGH,
                isCompleted = true,
                createdAt = Date(),
                modifiedAt = Date()
            )
        )
        override fun getItemsFlow(): Flow<List<TodoItem>> = flowOf(items)

        override suspend fun getItem(id: String): TodoItem? = items.find { it.id == id }

        override suspend fun addItem(item: TodoItem) {}

        override suspend fun saveItem(item: TodoItem) {}

        override suspend fun deleteItem(item: TodoItem) {}

        override suspend fun synchronize() {}
    }
    return TodoListViewModel(
        application = context.applicationContext as Application,
        todoItemRepository = mockRepository
    )
}