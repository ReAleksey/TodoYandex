package com.example.todoapp.view.items

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todoapp.R

@Composable
internal fun DeleteItem(
    enabled: Boolean,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    TextButton(
        onClick = {
            onClick()
            onDeleted()
        },
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.error,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        enabled = enabled
    ) {
        Icon(Icons.Filled.Delete, contentDescription = stringResource(id = R.string.delete))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(id = R.string.delete),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}