package com.example.todoapp.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.model.TodoImportance

@Composable
internal fun PrioritySelectorItem(
    importance: TodoImportance,
    onChanged: (TodoImportance) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    var menuOpened by remember { mutableStateOf(false) }

    val iconAndTextColor = when (importance) {
        TodoImportance.DEFAULT, TodoImportance.LOW -> MaterialTheme.colorScheme.onPrimaryContainer
        TodoImportance.HIGH -> MaterialTheme.colorScheme.error
    }


    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.priority),
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        TextButton(
            onClick = {
                onClick()
                menuOpened = true
            },
            colors = ButtonDefaults.textButtonColors(
                contentColor = iconAndTextColor
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (importance.logo != null) {
                    Icon(
                        painter = painterResource(id = importance.logo),
                        contentDescription = stringResource(id = importance.title),
                        tint = iconAndTextColor,
                        modifier = if (importance == TodoImportance.LOW) {
                            Modifier.graphicsLayer(rotationZ = -90f)
                        } else {
                            Modifier
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = stringResource(id = importance.title),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        DropdownMenu(
            expanded = menuOpened,
            onDismissRequest = { menuOpened = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            for (importanceValue in TodoImportance.entries) {
                val itemIconColor = when (importanceValue) {
                    TodoImportance.DEFAULT, TodoImportance.LOW -> MaterialTheme.colorScheme.onPrimaryContainer
                    TodoImportance.HIGH -> MaterialTheme.colorScheme.error
                }
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = importanceValue.title),
                            style = MaterialTheme.typography.bodyLarge,
                            color = itemIconColor
                        )
                    },
                    onClick = {
                        onChanged(importanceValue)
                        menuOpened = false
                    },
                    leadingIcon = {
                        importanceValue.logo?.let {
                            Icon(
                                painterResource(id = it),
                                contentDescription = stringResource(id = importanceValue.title),
                                tint = itemIconColor,
                                modifier = if (importanceValue == TodoImportance.LOW) {
                                    Modifier.graphicsLayer(rotationZ = -90f)
                                } else {
                                    Modifier
                                }
                            )
                        } ?: Spacer(modifier = Modifier)
                    }
                )
            }
        }
    }
}