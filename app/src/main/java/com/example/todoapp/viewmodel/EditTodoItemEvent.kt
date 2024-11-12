package com.example.todoapp.viewmodel

sealed class EditTodoItemEvent {
    data class ShowSnackbar(val message: String) : EditTodoItemEvent()
}