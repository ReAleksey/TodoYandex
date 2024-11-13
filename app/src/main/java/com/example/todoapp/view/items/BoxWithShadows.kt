package com.example.todoapp.view.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

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