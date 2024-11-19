package com.example.todoapp.ui.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.R
import com.example.todoapp.ui.view.items.TodoItemRow
import com.example.todoapp.ui.view.items.TodoListToolbar
import com.example.todoapp.ui.view.viewmodel.TodoListViewModel
import com.example.todoapp.utils.TodoListEvent
import com.example.todoapp.utils.TodoListUiState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable

@Serializable
data object TodoList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel,
    toEditItemScreen: (itemId: String?) -> Unit,
    onThemeChange: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val userPreferences by viewModel.userPreferencesFlow.collectAsStateWithLifecycle()

    val filterState = if (uiState is TodoListUiState.Loaded) {
        (uiState as TodoListUiState.Loaded).filterState
    } else {
        TodoListUiState.FilterState.ALL
    }

    var refreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            refreshing = true
            viewModel.retryFetchingData()
            refreshing = false
        }
    )

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(refreshing) {
        if (refreshing) {
            isLoading = true
        } else {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is TodoListEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        Crossfade(targetState = isLoading) { loading ->
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            } else {
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        TodoListToolbar(
                            scrollBehavior = scrollBehavior,
                            doneCount = when (val state = uiState) {
                                is TodoListUiState.Loaded -> state.doneCount
                                else -> 0
                            },
                            filterState = filterState,
                            onFilterChange = {
                                viewModel.updateShowCompleted(it == TodoListUiState.FilterState.ALL)
                            },
                            darkTheme = userPreferences.darkTheme,
                            onThemeChange = onThemeChange
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { toEditItemScreen(null) },
                            shape = CircleShape,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.inverseOnSurface
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = stringResource(id = R.string.add)
                            )
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
                            AnimatedVisibility(visible = !refreshing) {
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
                        }

                        is TodoListUiState.Error -> {
                            val message = (uiState as TodoListUiState.Error).message
                            ErrorScreen(
                                message = message,
                                onRetry = { viewModel.retryFetchingData() },
                                onUseOffline = { viewModel.useOfflineMode() },
                                modifier = Modifier.padding(paddingValues)
                            )
                        }

                        TodoListUiState.Offline -> {
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
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    onUseOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.error_connection),
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(text = stringResource(id = R.string.retry))
            }
            Button(
                onClick = onUseOffline,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(text = stringResource(id = R.string.use_offline))
            }
        }
    }
}

//
// @Preview(showBackground = true)
// @Composable
// private fun TodoListScreenLightPreview() {
//    ToDoAppTheme(darkTheme = false) {
//        TodoListScreen(
//            viewModel = previewViewModel(),
//            toEditItemScreen = {},
//            onThemeChange = {}
//        )
//    }
// }
//
// @Preview(showBackground = true)
// @Composable
// private fun TodoListScreenDarkPreview() {
//    ToDoAppTheme(darkTheme = true) {
//        TodoListScreen(
//            viewModel = previewViewModel(),
//            toEditItemScreen = {},
//            onThemeChange = {}
//        )
//    }
// }
//
// @Composable
// private fun previewViewModel(): TodoListViewModel {
//    val context = LocalContext.current
//    val mockRepository = object : TodoItemRepository {
//        private val items = listOf(
//            TodoItem(
//                id = "1",
//                text = "Todo 1",
//                importance = TodoImportance.DEFAULT,
//                isCompleted = false,
//                createdAt = Date(),
//                modifiedAt = Date()
//            ),
//            TodoItem(
//                id = "2",
//                text = "Todo 2",
//                importance = TodoImportance.HIGH,
//                isCompleted = true,
//                createdAt = Date(),
//                modifiedAt = Date()
//            )
//        )
//        override fun getItemsFlow(): Flow<List<TodoItem>> = flowOf(items)
//
//        override suspend fun getItem(id: String): TodoItem? = items.find { it.id == id }
//
//        override suspend fun addItem(item: TodoItem) {}
//
//        override suspend fun saveItem(item: TodoItem) {}
//
//        override suspend fun deleteItem(item: TodoItem) {}
//
//        override suspend fun synchronize() {}
//    }
//    val mockUserPreferencesRepository = object : UserPreferencesRepositoryInterface {
//        override val userPreferencesFlow: Flow<UserPreferences> = flowOf(
//            UserPreferences(darkTheme = false, showCompleted = true)
//        )
//
//        override suspend fun updateDarkTheme(darkTheme: Boolean) {}
//        override suspend fun updateShowCompleted(showCompleted: Boolean) {}
//        override suspend fun getInitialPreferences(): UserPreferences {
//            return UserPreferences(darkTheme = false, showCompleted = true)
//        }
//    }
//    return TodoListViewModel(
//        application = context.applicationContext as Application,
//        todoItemRepository = mockRepository,
//        userPreferencesRepository = mockUserPreferencesRepository
//    )
// }
//
// @Preview(showBackground = true)
// @Composable
// private fun ErrorScreenPreview() {
//    ToDoAppTheme {
//        ErrorScreen(
//            message = "Нет подключения к интернету",
//            onRetry = {},
//            onUseOffline = {}
//        )
//    }
// }
