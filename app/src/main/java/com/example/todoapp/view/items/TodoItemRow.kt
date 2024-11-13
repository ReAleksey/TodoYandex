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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.model.TodoImportance
import com.example.todoapp.model.TodoItem
import com.example.todoapp.ui.theme.ToDoAppTheme
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItemRow(
    item: TodoItem,
    onChecked: (Boolean) -> Unit,
    onDeleted: () -> Unit,
    onInfoClicked: () -> Unit,
) {
    BoxWithShadows(
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

