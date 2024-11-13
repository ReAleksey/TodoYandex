package com.example.todoapp.utils

sealed class TodoListEvent {
    data class ShowSnackbar(val message: String) : TodoListEvent()
}
