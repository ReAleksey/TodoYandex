package com.example.todoapp.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp
import androidx.compose.foundation.lazy.LazyListState
import com.example.todoapp.R
import com.example.todoapp.viewmodel.TodoListUiState


@Composable
internal fun TodoListToolbar(
    topPadding: Dp,
    doneCount: Int?,
    filterState: TodoListUiState.FilterState?,
    onFilterChange: (TodoListUiState.FilterState) -> Unit,
    lazyListState: LazyListState,
    content: @Composable () -> Unit
) {
    val maxHeight = 140.dp
    val minHeight = 56.dp

    val scrollProgress by remember {
        derivedStateOf {
            (lazyListState.firstVisibleItemScrollOffset / 200f).coerceIn(0f, 1f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding)
    ) {
        val currentHeight = lerp(maxHeight, minHeight, scrollProgress)
        val boxColor = getToolbarValue(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background,
            scrollProgress
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(currentHeight)
                .background(boxColor)
                .setShadow(scrollProgress)
        ) {
            val textSize = getToolbarValue(
                MaterialTheme.typography.displayLarge.fontSize.value,
                MaterialTheme.typography.headlineLarge.fontSize.value,
                scrollProgress
            ).sp
            val leftPadding = getToolbarValue(60f, 16f, scrollProgress).dp

            Text(
                text = stringResource(id = R.string.todo_title),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = leftPadding),
                fontSize = textSize,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.displayLarge
            )

            if (doneCount != null) {
                val countColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(
                    alpha = getToolbarValue(
                        MaterialTheme.colorScheme.onTertiaryContainer.alpha,
                        0f,
                        maxOf(0f, scrollProgress * 2 - 1f)
                    )
                )

                Text(
                    text = stringResource(id = R.string.done_count, doneCount),
                    modifier = Modifier
                        .align(if (scrollProgress < 0.5f) Alignment.BottomStart else Alignment.CenterStart)
                        .padding(
                            start = leftPadding,
                            bottom = if (scrollProgress < 0.5f) 20.dp else 0.dp
                        ),
                    color = countColor,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

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
                modifier = Modifier
                    .align(if (scrollProgress < 0.5f) Alignment.BottomEnd else Alignment.CenterEnd)
                    .padding(end = 16.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                ),
                enabled = filterState != null
            ) {
                FilterIcon(filterState = filterState)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = currentHeight)
        ) {
            content()
        }
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

@Composable
private fun FilterIcon(filterState: TodoListUiState.FilterState?) {
    if (filterState != null && filterState == TodoListUiState.FilterState.ALL) {
        Icon(
            painter = painterResource(id = R.drawable.visibility),
            contentDescription = stringResource(id = R.string.show)
        )
    } else {
        Icon(
            painter = painterResource(id = R.drawable.visibility_off),
            contentDescription = stringResource(id = R.string.hide)
        )
    }
}

