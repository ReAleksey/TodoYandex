package com.example.todoapp.utils

sealed class EditTodoItemEvent {
    data class ShowSnackbar(val message: String) : EditTodoItemEvent()
}