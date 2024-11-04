package com.example.todoapp.view.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todoapp.R

@Composable
internal fun TextFieldItem(
    text: String,
    onChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
) {
    val indicatorColor = Color.Transparent
    val containerColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer
    val placeFolderColor =  MaterialTheme.colorScheme.secondary
    val cursorColor = MaterialTheme.colorScheme.primaryContainer
    TextField(
        value = text,
        onValueChange = { onChanged(it) },
        modifier = modifier.shadow(2.dp),
        minLines = 5,
        placeholder = { Text(text = stringResource(id = R.string.text_field)) },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = indicatorColor,
            unfocusedIndicatorColor = indicatorColor,
            disabledIndicatorColor = indicatorColor,
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            disabledTextColor = textColor,
            focusedPlaceholderColor = placeFolderColor,
            disabledPlaceholderColor = placeFolderColor,
            unfocusedPlaceholderColor = placeFolderColor,
            cursorColor = cursorColor
        ),
        shape = shape,
        textStyle = MaterialTheme.typography.bodyLarge
    )
}