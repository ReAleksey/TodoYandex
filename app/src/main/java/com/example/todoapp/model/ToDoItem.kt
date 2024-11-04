package com.example.todoapp.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.todoapp.R
import java.util.Date


data class TodoItem(
    val id: String,
    val text: String,
    val importance: TodoImportance = TodoImportance.DEFAULT,
    val deadline: Date? = null,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val modifiedAt: Date? = null
)

enum class TodoImportance(
    @StringRes val title: Int,
    @DrawableRes val logo: Int? = null,
) {
    DEFAULT(
        title = R.string.priority_default
    ),
    LOW(
        title = R.string.priority_low,
        logo = R.drawable.arrow
    ),
    HIGH(
        title = R.string.priority_high,
        logo = R.drawable.priority
    )
}
