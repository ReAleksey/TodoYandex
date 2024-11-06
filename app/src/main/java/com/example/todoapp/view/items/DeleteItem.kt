package com.example.todoapp.view.items

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.ui.theme.ToDoAppTheme

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

@Preview(showBackground = true)
@Composable
private fun DeleteItemLightPreview() {
    ToDoAppTheme(darkTheme = false) {
        DeleteItem(
            enabled = true,
            onDeleted = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF161618) // md_theme_dark_background_primary)
@Composable
private fun DeleteItemDarkPreview() {
    ToDoAppTheme(darkTheme = true) {
        DeleteItem(
            enabled = true,
            onDeleted = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteItemDisabledLightPreview() {
    ToDoAppTheme(darkTheme = false) {
        DeleteItem(
            enabled = false,
            onDeleted = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF161618) // md_theme_dark_background_primary)
@Composable
private fun DeleteItemDisabledDarkPreview() {
    ToDoAppTheme(darkTheme = true) {
        DeleteItem(
            enabled = false,
            onDeleted = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}