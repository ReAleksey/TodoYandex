package com.example.todoapp.view

import androidx.compose.animation.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.ZoneId
import java.util.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import com.example.todoapp.view.items.DeadlineItem
import com.example.todoapp.view.items.DeleteItem
import com.example.todoapp.view.items.PrioritySelectorItem
import com.example.todoapp.R
import com.example.todoapp.utils.toDate
import com.example.todoapp.view.items.TextFieldItem
import com.example.todoapp.utils.toLocalDate
import com.example.todoapp.viewmodel.EditTodoItemUiState
import com.example.todoapp.viewmodel.EditTodoItemViewModel


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
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    val appBarColor = MaterialTheme.colorScheme.background
    val scrolledAppBarColor = MaterialTheme.colorScheme.surface

    val topColor = remember {
        Animatable(if (scrollState.canScrollBackward) appBarColor else scrolledAppBarColor)
    }

    val topElevation = remember {
        androidx.compose.animation.core.Animatable(if (scrollState.canScrollBackward) 10f else 0f)
    }

    LaunchedEffect(scrollState.canScrollBackward) {
        launch { topElevation.animateTo(if (scrollState.canScrollBackward) 10f else 0f) }
        launch { topColor.animateTo(if (scrollState.canScrollBackward) scrolledAppBarColor else appBarColor) }
    }

    Scaffold(
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
                                viewModel.save()
                                onClose()
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
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                },
                modifier = Modifier
                    .shadow(
                        elevation = topElevation.value.dp
                    )
                    .background(topColor.value),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = stringResource(id = R.string.close),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { paddingValue ->
        when (uiState) {
            is EditTodoItemUiState.Loaded -> {
                Column(
                    modifier = Modifier
                        .padding(
                            top = paddingValue.calculateTopPadding(),
                            start = 16.dp,
                            end = 16.dp
                        )
//                        .fillMaxHeight()
                        .verticalScroll(scrollState)
                        .focusable()
                ) {
                    val state = uiState as EditTodoItemUiState.Loaded

                    val inputShape = RoundedCornerShape(10.dp)

                    Spacer(modifier = Modifier.height(4.dp))
                    TextFieldItem(
                        text = state.item.text,
                        onChanged = { newText ->
                            viewModel.edit(item = state.item.copy(text = newText))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, inputShape),
                        shape = inputShape
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PrioritySelectorItem(
                        importance = state.item.importance,
                        onChanged = { importance ->
                            viewModel.edit(state.item.copy(importance = importance))
                        },
                        onClick = focusManager::clearFocus,
                    )
                    EdiItemSeparator(
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )
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
                        onClick = focusManager::clearFocus,
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    EdiItemSeparator(
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )
                    DeleteItem(
                        enabled = state.itemState == EditTodoItemUiState.ItemState.EDIT,
                        onDeleted = {
                            viewModel.delete()
                            onClose()
                        },
                        onClick = focusManager::clearFocus,
                    )
                    Spacer(modifier = Modifier.height(paddingValue.calculateBottomPadding()))
                }
            }

//            is EditTodoItemUiState.Error -> {
//                ErrorComponent(
//                    exception = (uiState as EditItemUiState.Error).exception,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(paddingValue)
//                )
//            }
//
//            EditItemUiState.Loading -> {
//                LoadingComponent(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(paddingValue)
//                )
//            }
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