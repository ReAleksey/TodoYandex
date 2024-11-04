package com.example.todoapp

import android.app.Application
import com.example.todoapp.model.TodoItemRepository
import com.example.todoapp.model.TodoItemsRepositoryImpl

class TodoApp: Application() {
    val todoItemRepository: TodoItemRepository by lazy { TodoItemsRepositoryImpl() }
}