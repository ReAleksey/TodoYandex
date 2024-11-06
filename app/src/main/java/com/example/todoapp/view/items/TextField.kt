package com.example.todoapp.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val placeFolderColor = MaterialTheme.colorScheme.secondary
    val cursorColor = MaterialTheme.colorScheme.primaryContainer

    BoxWithShadows (
        sides = Sides.LEFT_RIGHT_AND_BOTTOM
    ) {
        Box(
            modifier = Modifier
                .shadow(2.dp, shape)
                .clip(shape)
                .background(containerColor)
                .fillMaxWidth()
        ) {
            TextField(
                value = text,
                onValueChange = { onChanged(it) },
                modifier = modifier.fillMaxWidth(),
                minLines = 5,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.text_field),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
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
    }
}