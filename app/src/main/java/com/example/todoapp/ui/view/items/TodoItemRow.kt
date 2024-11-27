package com.example.todoapp.ui.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.todoapp.model.TodoItem

@Composable
fun TodoItemRow(
    item: TodoItem,
    onChecked: (Boolean) -> Unit,
    onDeleted: () -> Unit,
    onInfoClicked: () -> Unit,
    dismissOnCheck: Boolean
) {
    BoxWithShadows(
        sides = Sides.LEFT_AND_RIGHT,
    ) {
        RowSwipe(
            completed = item.isCompleted,
            dismissOnCheck = dismissOnCheck,
            onChecked = { onChecked(!item.isCompleted) },
            onDelete = onDeleted,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            TodoItemRowContent(
                item = item,
                onChecked = { onChecked(!item.isCompleted) },
                onInfoClicked = onInfoClicked,
                onRowClick = onInfoClicked,
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

