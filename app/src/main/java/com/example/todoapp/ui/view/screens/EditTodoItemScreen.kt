package com.example.todoapp.ui.view.screens

import androidx.compose.animation.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.model.TodoImportance
import com.example.todoapp.model.TodoItem
import com.example.todoapp.data.repository.TodoItemRepository
import com.example.todoapp.ui.theme.ToDoAppTheme
import com.example.todoapp.utils.toDate
import com.example.todoapp.utils.toLocalDate
import com.example.todoapp.ui.view.items.DeadlineItem
import com.example.todoapp.ui.view.items.DeleteItem
import com.example.todoapp.ui.view.items.PrioritySelectorItem
import com.example.todoapp.ui.view.items.TextFieldItem
import com.example.todoapp.utils.EditTodoItemEvent
import com.example.todoapp.ui.view.viewmodel.EditTodoItemUiState
import com.example.todoapp.ui.view.viewmodel.EditTodoItemViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.Date


@Serializable
data class EditTodoItem(
    val itemId: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoItemScreen(
    itemId: String?,
    viewModel: EditTodoItemViewModel,
    onClose: () -> Unit
) {
    viewModel.setItem(itemId)

    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val appBarColor = MaterialTheme.colorScheme.background
    val scrolledAppBarColor = MaterialTheme.colorScheme.surface
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val topColor = remember {
        Animatable(appBarColor)
    }

    val topElevation = remember {
        androidx.compose.animation.core.Animatable(0.dp.value)
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is EditTodoItemEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    LaunchedEffect(scrollBehavior.state.collapsedFraction) {
        val fraction = scrollBehavior.state.collapsedFraction
        launch {
            topElevation.animateTo(if (fraction > 0f) 3.dp.value else 0.dp.value)
        }
        launch {
            topColor.animateTo(if (fraction > 0f) scrolledAppBarColor else appBarColor)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                focusManager.clearFocus()
                                coroutineScope.launch {
                                    viewModel.save()
                                    onClose()
                                }
                            },
                            enabled = uiState is EditTodoItemUiState.Loaded,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primaryContainer,
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.save),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = stringResource(id = R.string.close),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                modifier = Modifier
                    .shadow(elevation = topElevation.value.dp)
                    .background(topColor.value),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValue ->
        when (uiState) {
            is EditTodoItemUiState.Loaded -> {
                val state = uiState as EditTodoItemUiState.Loaded
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = paddingValue.calculateTopPadding(),
                            start = 16.dp,
                            end = 16.dp,
                            bottom = paddingValue.calculateBottomPadding()
                        )
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    item {
                        TextFieldItem(
                            text = state.item.text,
                            onChanged = { newText ->
                                viewModel.edit(item = state.item.copy(text = newText))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    item {
                        PrioritySelectorItem(
                            importance = state.item.importance,
                            onChanged = { importance ->
                                viewModel.edit(state.item.copy(importance = importance))
                            },
                            onClick = focusManager::clearFocus
                        )
                    }

                    item {
                        EdiItemSeparator(
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        )
                    }

                    item {
                        DeadlineItem(
                            deadline = state.item.deadline.toLocalDate(),
                            onChanged = { newDeadline ->
                                viewModel.edit(
                                    state.item.copy(
                                        deadline = newDeadline?.toDate()
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            onClick = focusManager::clearFocus
                        )
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }

                    item {
                        EdiItemSeparator(
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        )
                    }

                    item {
                        DeleteItem(
                            enabled = state.itemState == EditTodoItemUiState.ItemState.EDIT,
                            onDeleted = {
                                coroutineScope.launch {
                                    viewModel.delete()
                                    onClose()
                                }
                            },
                            onClick = focusManager::clearFocus
                        )
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun EdiItemSeparator(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.outline
    )
}

@Preview(showBackground = true)
@Composable
private fun EditTodoItemScreenLightPreview() {
    ToDoAppTheme(darkTheme = false) {
        EditTodoItemScreen(
            itemId = "1",
            viewModel = previewViewModel(),
            onClose = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditTodoItemScreenDarkPreview() {
    ToDoAppTheme(darkTheme = true) {
        EditTodoItemScreen(
            itemId = "1",
            viewModel = previewViewModel(),
            onClose = {}
        )
    }
}

@Composable
private fun previewViewModel(): EditTodoItemViewModel {
    val viewModel = EditTodoItemViewModel(
        todoItemRepository = object : TodoItemRepository {
            override fun getItemsFlow(): StateFlow<List<TodoItem>> =
                MutableStateFlow(emptyList())

            override suspend fun getItem(id: String): TodoItem = TodoItem(
                id = "1",
                text = "Тестовая заметка",
                importance = TodoImportance.HIGH,
                deadline = Date(),
                isCompleted = false
            )

            override suspend fun addItem(item: TodoItem) {}
            override suspend fun saveItem(item: TodoItem) {}
            override suspend fun deleteItem(item: TodoItem) {}
            override suspend fun synchronize() {}
        }
    )

    viewModel.setItem("1")
    return viewModel
}