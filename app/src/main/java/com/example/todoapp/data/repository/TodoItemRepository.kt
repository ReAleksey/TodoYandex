package com.example.todoapp.data.repository

import com.example.todoapp.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoItemRepository {
    fun getItemsFlow(): Flow<List<TodoItem>>

    suspend fun getItem(id: String): TodoItem?

    suspend fun addItem(item: TodoItem)

    suspend fun saveItem(item: TodoItem)

    suspend fun deleteItem(item: TodoItem)

    suspend fun synchronize()
}