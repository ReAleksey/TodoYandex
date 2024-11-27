package com.example.todoapp.utils

import com.example.todoapp.data.network.ImportanceNetwork
import com.example.todoapp.data.network.TodoItemNetwork
import com.example.todoapp.model.TodoImportance
import com.example.todoapp.model.TodoItem
import java.util.Date

fun TodoItem.toNetworkModel(deviceId: String): TodoItemNetwork {
    val milliseconds = 1000
    return TodoItemNetwork(
        id = id,
        text = text,
        importance = importance.toNetworkImportance(),
        deadline = deadline?.time?.div(milliseconds),
        isCompleted = isCompleted,
        color = null,
        createdAt = createdAt.time.div(milliseconds),
        changedAt = modifiedAt?.time?.div(milliseconds) ?: createdAt.time.div(milliseconds),
        lastUpdatedBy = deviceId
    )
}

fun TodoItemNetwork.toDomainModel(): TodoItem {
    val milliseconds = 1000
    return TodoItem(
        id = id,
        text = text,
        importance = importance.toDomainImportance(),
        deadline = deadline?.let { Date(it * milliseconds) },
        isCompleted = isCompleted,
        createdAt = Date(createdAt * milliseconds),
        modifiedAt = Date(changedAt * milliseconds)
    )
}

fun TodoImportance.toNetworkImportance(): ImportanceNetwork {
    return when (this) {
        TodoImportance.LOW -> ImportanceNetwork.LOW
        TodoImportance.DEFAULT -> ImportanceNetwork.BASIC
        TodoImportance.HIGH -> ImportanceNetwork.IMPORTANT
    }
}

fun ImportanceNetwork.toDomainImportance(): TodoImportance {
    return when (this) {
        ImportanceNetwork.LOW -> TodoImportance.LOW
        ImportanceNetwork.BASIC -> TodoImportance.DEFAULT
        ImportanceNetwork.IMPORTANT -> TodoImportance.HIGH
    }
}
