package com.example.todoapp.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoItemNetwork(
    @SerialName("id") val id: String,
    @SerialName("text") val text: String,
    @SerialName("importance") val importance: ImportanceNetwork,
    @SerialName("deadline") val deadline: Long? = null,
    @SerialName("done") val isCompleted: Boolean,
    @SerialName("color") val color: String? = null,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("changed_at") val changedAt: Long,
    @SerialName("last_updated_by") val lastUpdatedBy: String
)

@Serializable
enum class ImportanceNetwork {
    @SerialName("low")
    LOW,

    @SerialName("basic")
    BASIC,

    @SerialName("important")
    IMPORTANT
}

@Serializable
data class TodoListResponse(
    val status: String,
    val list: List<TodoItemNetwork>,
    val revision: Int
)

@Serializable
data class TodoItemResponse(
    val status: String,
    val element: TodoItemNetwork,
    val revision: Int
)

@Serializable
data class TodoItemRequest(
    val element: TodoItemNetwork
)

@Serializable
data class TodoListRequest(
    val list: List<TodoItemNetwork>
)

