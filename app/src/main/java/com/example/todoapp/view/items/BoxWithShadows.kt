package com.example.todoapp.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.ui.theme.ToDoAppTheme

internal enum class Sides {
    LEFT_AND_RIGHT,
    BOTTOM,
    TOP,
    LEFT_RIGHT_AND_BOTTOM
}

@Composable
internal fun BoxWithShadows(
    sides: Sides,
    content: @Composable () -> Unit
) {
    val shadowShape =
        when (sides) {
            Sides.LEFT_AND_RIGHT, Sides.BOTTOM -> GenericShape { size, _ ->
                val maxSize = (size.width + size.height)
                moveTo(-maxSize, 0f)
                lineTo(maxSize, 0f)
                lineTo(maxSize, size.height + maxSize)
                lineTo(0f, size.height + maxSize)
            }

            Sides.TOP -> GenericShape { size, _ ->
                val maxSize = (size.width + size.height)
                moveTo(-maxSize, -maxSize)
                lineTo(maxSize, -maxSize)
                lineTo(maxSize, size.height + maxSize)
                lineTo(0f, size.height + maxSize)
            }
            Sides.LEFT_RIGHT_AND_BOTTOM -> GenericShape { size, _ ->
                val maxSize = (size.width + size.height)
                moveTo(-maxSize, 0f)
                lineTo(maxSize, 0f)
                lineTo(maxSize, size.height + maxSize)
                lineTo(-maxSize, size.height + maxSize)
            }
        }

    Box(
        modifier = Modifier
            .clip(shadowShape)
    ) {
        content()
    }
}