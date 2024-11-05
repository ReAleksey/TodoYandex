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
import com.example.todoapp.view.items.BoxWithSidesForShadow
import com.example.todoapp.R
import com.example.todoapp.view.items.Sides
import com.example.todoapp.view.items.TodoItemRow
import com.example.todoapp.view.items.TodoListToolbar
import com.example.todoapp.viewmodel.TodoListUiState
import com.example.todoapp.viewmodel.TodoListViewModel
import kotlinx.serialization.Serializable

@Serializable
data object TodoList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel,
    toEditItemScreen: (itemId: String?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TodoListToolbar(
                scrollBehavior = scrollBehavior,
                topPadding = 0.dp,
                doneCount = (uiState as? TodoListUiState.Loaded)?.doneCount,
                filterState = (uiState as? TodoListUiState.Loaded)?.filterState,
                onFilterChange = viewModel::onFilterChange,
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
                    userScrollEnabled = true,
                    state = lazyListState
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
                                BoxWithSidesForShadow(
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
                                BoxWithSidesForShadow(
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
//                is TodoListUiState.Error -> {
//                    ErrorComponent(
//                        exception = (uiState as TodoListUiState.Error).exception,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(paddingValue)
//                    )
//                }
//
//                TodoListUiState.Loading -> {
//                    LoadingComponent(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(paddingValue)
//                    )
//                }