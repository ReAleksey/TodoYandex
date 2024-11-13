package com.example.todoapp.utils

import com.example.todoapp.model.TodoItem

sealed class TodoListUiState {
    data object Loading : TodoListUiState()
    data class Error(val message: String) : TodoListUiState()

    data class Loaded(
        val items: List<TodoItem>,
        val filterState: FilterState,
        val doneCount: Int
    ) : TodoListUiState()

    object Offline : TodoListUiState()

    enum class FilterState(val filter: (TodoItem) -> Boolean) {
        ALL({ true }),
        NOT_COMPLETED({ !it.isCompleted })
    }
}
