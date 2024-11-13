package com.example.todoapp.view.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R
import com.example.todoapp.ui.theme.ToDoAppTheme
import com.example.todoapp.viewmodel.TodoListUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoListToolbar(
    scrollBehavior: TopAppBarScrollBehavior,
    topPadding: Dp,
    doneCount: Int?,
    filterState: TodoListUiState.FilterState?,
    onFilterChange: (TodoListUiState.FilterState) -> Unit,
    darkTheme: Boolean,
    onThemeChange: () -> Unit
) {
    val countColor = MaterialTheme.colorScheme.onSurfaceVariant

    val progress = 1f - (scrollBehavior.state.collapsedFraction)

    val textSize = getToolbarValue(
        MaterialTheme.typography.displayLarge.fontSize.value,
        MaterialTheme.typography.headlineLarge.fontSize.value,
        progress
    ).sp

    val boxColor = getToolbarValue(
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.background,
        progress
    )

    LargeTopAppBar(
        modifier = Modifier
            .padding(top = topPadding)
            .setShadow(progress),
        windowInsets = WindowInsets(
            left = 0.dp,
            top = 0.dp,
            right = 0.dp,
            bottom = 4.dp
        ),
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.todo_title),
                        fontSize = textSize,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 60.dp)
                    )
                    if (progress < 0.5f) {
                        Row {
                            ThemeIconButton(
                                darkTheme = darkTheme,
                                onThemeChange = onThemeChange
                            )
                            FilterIconButton(
                                filterState = filterState,
                                onFilterChange = onFilterChange
                            )
                        }
                    }
                }
                if (doneCount != null && progress > 0.5f) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.done_count, doneCount),
                            color = countColor,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 60.dp)
                        )
                        Row {
                            ThemeIconButton(
                                darkTheme = darkTheme,
                                onThemeChange = onThemeChange
                            )
                            FilterIconButton(
                                filterState = filterState,
                                onFilterChange = onFilterChange
                            )
                        }
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = boxColor,
            scrolledContainerColor = boxColor
        )
    )
}

private fun Modifier.setShadow(progress: Float) =
    drawBehind {
        val size = size

        val shadowStart = Color.Black.copy(
            alpha = getToolbarValue(
                0.0f,
                0.32f,
                progress
            )
        )
        val shadowEnd = Color.Transparent

        if (progress < 1f) {
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(shadowStart, shadowEnd),
                    startY = size.height,
                    endY = size.height + 28f
                ),
                topLeft = Offset(0f, size.height),
                size = Size(size.width, 28f),
            )
        }
    }

private fun getToolbarValue(startValue: Float, endValue: Float, progress: Float) =
    endValue + (startValue - endValue) * progress

private fun getToolbarValue(startValue: Color, endValue: Color, progress: Float) =
    Color(
        red = getToolbarValue(startValue.red, endValue.red, progress),
        blue = getToolbarValue(startValue.blue, endValue.blue, progress),
        green = getToolbarValue(startValue.green, endValue.green, progress),
    )
@Composable
private fun FilterIcon(filterState: TodoListUiState.FilterState?) {
    if (filterState != null && filterState == TodoListUiState.FilterState.ALL) {
        Icon(
            painter = painterResource(id = R.drawable.visibility_off),
            contentDescription = stringResource(id = R.string.show),
            tint = MaterialTheme.colorScheme.primaryContainer
        )
    } else {
        Icon(
            painter = painterResource(id = R.drawable.visibility),
            contentDescription = stringResource(id = R.string.hide),
            tint = MaterialTheme.colorScheme.primaryContainer
        )
    }
}
@Composable
private fun FilterIconButton(
    filterState: TodoListUiState.FilterState?,
    onFilterChange: (TodoListUiState.FilterState) -> Unit
) {
    IconButton(
        onClick = {
            onFilterChange(
                if (filterState == TodoListUiState.FilterState.ALL) {
                    TodoListUiState.FilterState.NOT_COMPLETED
                } else {
                    TodoListUiState.FilterState.ALL
                }
            )
        },
        enabled = filterState != null
    ) {
        FilterIcon(filterState = filterState)
    }
}

@Composable
private fun ThemeIcon(darkTheme: Boolean) {
    Icon(
        painter = painterResource(id = if (darkTheme) R.drawable.light_mode else R.drawable.night_mode),
        contentDescription = stringResource(
            id = if (darkTheme) R.string.light_theme else R.string.dark_theme
        ),
        tint = MaterialTheme.colorScheme.primaryContainer
    )
}

@Composable
private fun ThemeIconButton(
    darkTheme: Boolean,
    onThemeChange: () -> Unit
) {
    IconButton(
        onClick = onThemeChange
    ) {
        ThemeIcon(darkTheme = darkTheme)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun TodoListToolbarLightPreview() {
    ToDoAppTheme(darkTheme = false) {
        TodoListToolbar(
            scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
            topPadding = 0.dp,
            doneCount = 5,
            filterState = TodoListUiState.FilterState.ALL,
            onFilterChange = {},
            darkTheme = false,
            onThemeChange = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF161618)
@Composable
private fun TodoListToolbarDarkPreview() {
    ToDoAppTheme(darkTheme = true) {
        TodoListToolbar(
            scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
            topPadding = 0.dp,
            doneCount = 5,
            filterState = TodoListUiState.FilterState.NOT_COMPLETED,
            onFilterChange = {},
            darkTheme = true,
            onThemeChange = {}
        )
    }
}



