package com.example.todoapp.model

import kotlinx.coroutines.flow.StateFlow

interface TodoItemRepository {
    fun getItemsFlow(): StateFlow<List<TodoItem>>

    suspend fun getItem(id: String): TodoItem?

    suspend fun addItem(item: TodoItem)

    suspend fun saveItem(item: TodoItem)

    suspend fun deleteItem(item: TodoItem)

    suspend fun synchronize()
}