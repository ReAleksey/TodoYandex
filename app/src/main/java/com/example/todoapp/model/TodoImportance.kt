package com.example.todoapp.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.todoapp.R

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