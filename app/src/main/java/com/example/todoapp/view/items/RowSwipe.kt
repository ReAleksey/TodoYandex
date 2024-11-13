package com.example.todoapp.view.items

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowSwipe(
    modifier: Modifier = Modifier,
    completed: Boolean = false,
    dismissOnCheck: Boolean = false,
    onChecked: () -> Unit = {},
    onDelete: () -> Unit = {},
    content: @Composable (SwipeToDismissBoxState) -> Unit
) {
    var deleted by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    checked = true
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    deleted = true
                    true
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        },
        positionalThreshold = { it * .25f }
    )

    LaunchedEffect(deleted) {
        if (deleted) {
            delay(500)
            onDelete()
        }
    }

    LaunchedEffect(checked) {
        if (checked) {
            if (dismissOnCheck)
                delay(500)
            checked = false
            onChecked()
        }
    }

    AnimatedVisibility(
        visible = !(deleted || (dismissOnCheck && checked)),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 500),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = dismissState,
            modifier = modifier,
            backgroundContent = {
                SwipeBackground(dismissState = dismissState)
            },
            enableDismissFromStartToEnd = !completed
        ) {
            content(dismissState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primary
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
            Icon(Icons.Filled.Check,
                contentDescription = stringResource(id = R.string.checked),
                tint = MaterialTheme.colorScheme.inverseOnSurface
            )
        Spacer(modifier = Modifier)
        if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
            Icon(Icons.Filled.Delete,
                contentDescription = stringResource(id = R.string.delete),
                tint = MaterialTheme.colorScheme.inverseOnSurface
            )
    }
}
