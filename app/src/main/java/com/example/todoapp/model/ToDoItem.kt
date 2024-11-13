package com.example.todoapp.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.R
import java.util.Date

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "importance")
    val importance: TodoImportance = TodoImportance.DEFAULT,

    @ColumnInfo(name = "deadline")
    val deadline: Date? = null,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),

    @ColumnInfo(name = "modified_at")
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
