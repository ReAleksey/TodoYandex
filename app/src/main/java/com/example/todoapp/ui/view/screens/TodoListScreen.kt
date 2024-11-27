package com.example.todoapp.ui.view.screens

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.R
import com.example.todoapp.data.network.NetworkStatusProvider
import com.example.todoapp.data.repository.TodoItemRepository
import com.example.todoapp.data.repository.UserPreferences
import com.example.todoapp.data.repository.UserPreferencesRepositoryInterface
import com.example.todoapp.model.TodoImportance
import com.example.todoapp.model.TodoItem
import com.example.todoapp.ui.theme.ToDoAppTheme
import com.example.todoapp.ui.view.items.BoxWithShadows
import com.example.todoapp.ui.view.items.Sides
import com.example.todoapp.ui.view.items.TodoItemRow
import com.example.todoapp.ui.view.items.TodoListToolbar
import com.example.todoapp.ui.view.viewmodel.TodoListViewModel
import com.example.todoapp.utils.TodoListEvent
import com.example.todoapp.utils.TodoListUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.Serializable
import java.util.Date

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
        Crossfade(
            targetState = isLoading,
            animationSpec = tween(
                durationMillis = 1500,
                easing = LinearEasing
            )
        ) { loading ->
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
                                    item {
                                        Spacer(modifier = Modifier.height(5.dp))
                                    }
                                    if (state.items.isNotEmpty()) {
                                        item {
                                            val shape = RoundedCornerShape(
                                                topStart = 8.dp,
                                                topEnd = 8.dp
                                            )
                                            BoxWithShadows(
                                                sides = Sides.TOP
                                            ) {
                                                Spacer(
                                                    modifier = Modifier
                                                        .shadow(2.dp, shape)
                                                        .clip(shape)
                                                        .background(MaterialTheme.colorScheme.surface)
                                                        .fillMaxWidth()
                                                        .height(7.dp)
                                                )
                                            }
                                        }
                                        itemsIndexed(state.items, key = { _, item -> item.id }) { index, item ->
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
                                                },
                                                dismissOnCheck = (filterState == TodoListUiState.FilterState.NOT_COMPLETED)
                                            )
                                        }
                                        item {
                                            val shape = RoundedCornerShape(
                                                bottomStart = 8.dp,
                                                bottomEnd = 8.dp
                                            )
                                            BoxWithShadows(
                                                sides = Sides.BOTTOM
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .shadow(2.dp, shape)
                                                        .clip(shape)
                                                        .background(MaterialTheme.colorScheme.surface)
                                                        .clickable { toEditItemScreen(null) }
                                                ) {
                                                    Spacer(modifier = Modifier.width(45.dp))
                                                    Text(
                                                        text = stringResource(id = R.string.new_item),
                                                        modifier = Modifier.padding(20.dp),
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        item {
                                            val shape = RoundedCornerShape(8.dp)
                                            Row(
                                                modifier = Modifier
                                                    .shadow(2.dp, shape)
                                                    .clip(shape)
                                                    .background(MaterialTheme.colorScheme.surface)
                                                    .fillMaxWidth()
                                                    .clickable { toEditItemScreen(null) }
                                            ) {
                                                Spacer(modifier = Modifier.width(45.dp))
                                                Text(
                                                    text = stringResource(id = R.string.new_item),
                                                    modifier = Modifier.padding(20.dp),
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            }
                                        }
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(32.dp))
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
                                        },
                                        dismissOnCheck = (filterState == TodoListUiState.FilterState.NOT_COMPLETED)
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




@Preview(showBackground = true)
@Composable
private fun TodoListScreenLightPreview() {
    ToDoAppTheme(darkTheme = false) {
        TodoListScreen(
            viewModel = previewViewModel(),
            toEditItemScreen = {},
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
    val mockUserPreferencesRepository = object : UserPreferencesRepositoryInterface {
        override val userPreferencesFlow: Flow<UserPreferences> = flowOf(
            UserPreferences(darkTheme = false, showCompleted = true)
        )

        override suspend fun updateDarkTheme(darkTheme: Boolean) {}
        override suspend fun updateShowCompleted(showCompleted: Boolean) {}
        override suspend fun getInitialPreferences(): UserPreferences {
            return UserPreferences(darkTheme = false, showCompleted = true)
        }
    }

    val mockNetworkStatusTracker = object : NetworkStatusProvider {
        override val networkStatus: Flow<Boolean> = flowOf(true)
    }

    return TodoListViewModel(
        todoItemRepository = mockRepository,
        userPreferencesRepository = mockUserPreferencesRepository,
        networkStatusTracker = mockNetworkStatusTracker
    )
}


