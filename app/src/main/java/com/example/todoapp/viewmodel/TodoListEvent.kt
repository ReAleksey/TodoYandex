package com.example.todoapp.viewmodel

sealed class TodoListEvent {
    data class ShowSnackbar(val message: String) : TodoListEvent()
}
