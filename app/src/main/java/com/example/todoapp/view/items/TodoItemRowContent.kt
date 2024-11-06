package com.example.todoapp.view.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.model.TodoImportance
import com.example.todoapp.model.TodoItem
import com.example.todoapp.utils.toLocalDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun TodoItemRowContent(
    item: TodoItem,
    onChecked: (Boolean) -> Unit,
    onInfoClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemCheckBox(
            completed = item.isCompleted,
            highImportance = item.importance == TodoImportance.HIGH,
            onChecked = { value -> onChecked(value) }
        )
        Spacer(modifier = Modifier.width(4.dp))
        if (item.importance.logo != null) {
            ImportanceIcon(importance = item.importance)
            Spacer(modifier = Modifier.width(5.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            ItemText(
                text = item.text,
                completed = item.isCompleted,
            )
            if (item.deadline != null) {
                Spacer(modifier = Modifier.height(8.dp))
                DeadlineText(deadline = item.deadline.toLocalDate()!!)
            }
        }
        InfoIconButton(
            onInfoClicked = onInfoClicked
        )
    }
}

@Composable
private fun ImportanceIcon(importance: TodoImportance, modifier: Modifier = Modifier) {
    val iconColor = when (importance) {
        TodoImportance.LOW -> MaterialTheme.colorScheme.secondary
        TodoImportance.HIGH -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }
    Icon(
        painter = painterResource(id = importance.logo!!),
        contentDescription = stringResource(id = importance.title),
        modifier = modifier.then(
            if (importance == TodoImportance.LOW) {
                Modifier.graphicsLayer(rotationZ = -90f)
            } else {
                Modifier
            }
        ),
        tint = iconColor
    )
}

@Composable
private fun InfoIconButton(onInfoClicked: () -> Unit) {
    IconButton(
        onClick = { onInfoClicked() },
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Icon(Icons.Outlined.Info, contentDescription = R.string.info.toString())
    }
}

@Composable
private fun DeadlineText(deadline: LocalDate, modifier: Modifier = Modifier) {
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    Text(
        text = dateFormatter.format(deadline),
        modifier = modifier,
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun ItemText(text: String, completed: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        textDecoration =
        if (completed)
            TextDecoration.LineThrough
        else
            null,
        color = if (completed)
            MaterialTheme.colorScheme.onTertiaryContainer
        else
            MaterialTheme.colorScheme.onPrimaryContainer,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun ItemCheckBox(
    completed: Boolean,
    highImportance: Boolean,
    onChecked: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Checkbox(
        checked = completed,
        onCheckedChange = { onChecked(it) },
        modifier = modifier,
        colors =
        if (highImportance)
            CheckboxColors(
                checkedCheckmarkColor = MaterialTheme.colorScheme.surface,
                checkedBoxColor = MaterialTheme.colorScheme.primary,
                uncheckedBoxColor = MaterialTheme.colorScheme.error.copy(alpha = 0.16f),
                checkedBorderColor = MaterialTheme.colorScheme.primary,
                uncheckedBorderColor = MaterialTheme.colorScheme.error,
                uncheckedCheckmarkColor = Color.Unspecified,
                disabledCheckedBoxColor = Color.Unspecified,
                disabledUncheckedBoxColor = Color.Unspecified,
                disabledIndeterminateBoxColor = Color.Unspecified,
                disabledBorderColor = Color.Unspecified,
                disabledUncheckedBorderColor = Color.Unspecified,
                disabledIndeterminateBorderColor = Color.Unspecified,
            )
        else
            CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline,
                checkmarkColor = MaterialTheme.colorScheme.surface
            )
    )
}