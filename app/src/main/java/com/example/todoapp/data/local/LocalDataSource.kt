package com.example.todoapp.data.local

import com.example.todoapp.model.TodoItem
import com.example.todoapp.model.TodoItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val todoItemDao: TodoItemDao
){

    fun getAllItems(): Flow<List<TodoItem>> = todoItemDao.getAllItems()

    suspend fun getItemById(id: String): TodoItem? = todoItemDao.getItemById(id)

    suspend fun insertItem(item: TodoItem) {
        todoItemDao.insertItem(item)
    }

    suspend fun updateItem(item: TodoItem) {
        todoItemDao.updateItem(item)
    }

    suspend fun deleteItem(item: TodoItem) {
        todoItemDao.deleteItem(item)
    }
    suspend fun deleteAllItems() {
        todoItemDao.deleteAllItems()
    }

    suspend fun insertItems(items: List<TodoItem>) {
        todoItemDao.insertItems(items)
    }

    suspend fun getCurrentItems(): List<TodoItem> = todoItemDao.getAllItems().first()
}