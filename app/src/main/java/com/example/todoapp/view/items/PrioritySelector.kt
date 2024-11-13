package com.example.todoapp.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.model.TodoImportance
import com.example.todoapp.ui.theme.ToDoAppTheme

@Composable
internal fun PrioritySelectorItem(
    importance: TodoImportance,
    onChanged: (TodoImportance) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.priority),
            style = MaterialTheme.typography.bodyLarge
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(TodoImportance.LOW, TodoImportance.DEFAULT, TodoImportance.HIGH).forEach { importanceValue ->
                val isSelected = importance == importanceValue
                val itemIconColor = when (importanceValue) {
                    TodoImportance.DEFAULT, TodoImportance.LOW -> MaterialTheme.colorScheme.onPrimaryContainer
                    TodoImportance.HIGH -> MaterialTheme.colorScheme.error
                }

                Box(
                    modifier = Modifier
                        .then(
                            if (isSelected) {
                                Modifier.shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(4.dp),
                                    clip = false
                                )
                            } else Modifier
                        )
                ) {
                    IconButton(
                        onClick = {
                            onClick()
                            onChanged(importanceValue)
                        },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        if (importanceValue == TodoImportance.DEFAULT) {
                            Text(
                                text = stringResource(id = R.string.no),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) {
                                    itemIconColor
                                } else {
                                    itemIconColor.copy(alpha = 0.4f)
                                }
                            )
                        } else {
                            importanceValue.logo?.let {
                                Icon(
                                    painter = painterResource(id = it),
                                    contentDescription = stringResource(id = importanceValue.title),
                                    tint = if (isSelected) {
                                        itemIconColor
                                    } else {
                                        itemIconColor.copy(alpha = 0.4f)
                                    },
                                    modifier = if (importanceValue == TodoImportance.LOW) {
                                        Modifier
                                            .graphicsLayer(rotationZ = -90f)
                                    } else {
                                        Modifier
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrioritySelectorItemLightPreview() {
    var importance by remember { mutableStateOf(TodoImportance.DEFAULT) }

    ToDoAppTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            PrioritySelectorItem(
                importance = importance,
                onChanged = { importance = it }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF161618) // md_theme_dark_background_primary
@Composable
private fun PrioritySelectorItemDarkPreview() {
    var importance by remember { mutableStateOf(TodoImportance.DEFAULT) }

    ToDoAppTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            PrioritySelectorItem(
                importance = importance,
                onChanged = { importance = it }
            )
        }
    }
}