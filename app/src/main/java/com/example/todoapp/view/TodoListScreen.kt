package com.example.todoapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

    // Обработка ошибок
    LaunchedEffect(uiState) {
        if (uiState is TodoListUiState.Error) {
            snackbarHostState.showSnackbar("Something went wrong")
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
                doneCount = (uiState as? TodoListUiState.Loaded)?.doneCount,
                filterState = (uiState as? TodoListUiState.Loaded)?.filterState,
                onFilterChange = viewModel::onFilterChange,
                darkTheme = darkTheme,
                onThemeChange = onThemeChange
            )
        },
        floatingActionButton = {
            if (uiState is TodoListUiState.Loaded)
                FloatingActionButton(
                    onClick = { toEditItemScreen(null) },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add))
                }
        }
    ) { paddingValue ->
        when (uiState) {
            is TodoListUiState.Loaded -> {
                val state = uiState as TodoListUiState.Loaded
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = paddingValue.calculateTopPadding()
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
                                    topEnd = 8.dp,
                                    topStart = 8.dp
                                )
                                BoxWithShadows (
                                    Sides.TOP,
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
                            items(state.items.size, key = { i -> state.items[i].hashCode() }) {
                                val item = state.items[it]
                                TodoItemRow(
                                    item = item,
                                    onChecked = { value -> viewModel.onChecked(item, value) },
                                    onDeleted = { viewModel.delete(item) },
                                    onInfoClicked = { toEditItemScreen(item.id) },
//                                    dismissOnCheck = state.filterState == TodoListUiState.FilterState.NOT_COMPLETED
                                )
                            }
                            item {
                                val shape = RoundedCornerShape(
                                    bottomEnd = 8.dp,
                                    bottomStart = 8.dp
                                )
                                BoxWithShadows (
                                    Sides.BOTTOM,
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
                                val shape = RoundedCornerShape(
                                    bottomEnd = 8.dp,
                                    bottomStart = 8.dp,
                                    topEnd = 8.dp,
                                    topStart = 8.dp
                                )
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
                            Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
            else -> {}
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
    return TodoListViewModel(
        todoItemRepository = TodoItemsRepositoryImpl()
    )
}