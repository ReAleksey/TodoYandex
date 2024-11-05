package com.example.todoapp.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.todoapp.model.TodoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItemRow(
    item: TodoItem,
    onChecked: (Boolean) -> Unit,
    onDeleted: () -> Unit,
    onInfoClicked: () -> Unit,
) {
    BoxWithSidesForShadow(
        sides = Sides.LEFT_AND_RIGHT,
    ) {
        RowSwipe(
            completed = item.isCompleted,
            onChecked = { onChecked(true) },
            onDelete = onDeleted,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            TodoItemRowContent(
                item = item,
                onChecked = onChecked,
                onInfoClicked = onInfoClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 0.dp,
                        bottom = 4.dp
                    )
            )
        }
    }
}


@Composable
internal fun BoxWithSidesForShadow(
    sides: Sides,
    content: @Composable () -> Unit
) {
    val shadowShape =
        when (sides) {
            Sides.LEFT_AND_RIGHT, Sides.BOTTOM -> GenericShape { size, _ ->
                val maxSize = (size.width + size.height) * 10
                moveTo(-maxSize, 0f)
                lineTo(maxSize, 0f)
                lineTo(maxSize, size.height + maxSize)
                lineTo(0f, size.height + maxSize)
            }

            Sides.TOP -> GenericShape { size, _ ->
                val maxSize = (size.width + size.height) * 10
                moveTo(-maxSize, -maxSize)
                lineTo(maxSize, -maxSize)
                lineTo(maxSize, size.height + maxSize)
                lineTo(0f, size.height + maxSize)
            }
        }

    Box(
        modifier = Modifier
            .clip(shadowShape)
    ) {
        content()
    }
}

internal enum class Sides {
    LEFT_AND_RIGHT,
    BOTTOM,
    TOP
}