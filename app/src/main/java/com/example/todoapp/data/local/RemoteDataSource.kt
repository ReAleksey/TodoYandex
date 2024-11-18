package com.example.todoapp.data.remote

import com.example.todoapp.data.network.TodoApiService
import com.example.todoapp.data.network.TodoItemRequest
import com.example.todoapp.data.network.TodoListRequest
import com.example.todoapp.data.network.TodoListResponse
import com.example.todoapp.model.TodoItem
import com.example.todoapp.utils.toDomainModel
import com.example.todoapp.utils.toNetworkModel

class RemoteDataSource(
    private val apiService: TodoApiService,
    private val deviceId: String
) {
    private var revision: Int = 0

    suspend fun getTodoList(): TodoListResponse {
        val response = apiService.getTodoList()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                revision = body.revision
                return body
            } else {
                throw Exception("Empty response body")
            }
        } else {
            throw Exception("Failed to fetch data from server: ${response.code()}")
        }
    }

    suspend fun updateTodoList(items: List<TodoItem>) {
        val request = TodoListRequest(items.map { it.toNetworkModel(deviceId) })
        val response = apiService.updateTodoList(revision, request)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                revision = body.revision
            } else {
                throw Exception("Empty response body")
            }
        } else {
            throw Exception("Failed to update server data: ${response.code()}")
        }
    }

    suspend fun addTodoItem(item: TodoItem) {
        val request = TodoItemRequest(item.toNetworkModel(deviceId))
        val response = apiService.addTodoItem(revision, request)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                revision = body.revision
            } else {
                throw Exception("Empty response body")
            }
        } else {
            throw Exception("Failed to add item: ${response.code()}")
        }
    }

    suspend fun updateTodoItem(item: TodoItem) {
        val request = TodoItemRequest(item.toNetworkModel(deviceId))
        val response = apiService.updateTodoItem(revision, item.id, request)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                revision = body.revision
            } else {
                throw Exception("Empty response body")
            }
        } else {
            throw Exception("Failed to update item: ${response.code()}")
        }
    }

    suspend fun deleteTodoItem(itemId: String) {
        val response = apiService.deleteTodoItem(revision, itemId)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                revision = body.revision
            } else {
                throw Exception("Empty response body")
            }
        } else {
            throw Exception("Failed to delete item: ${response.code()}")
        }
    }

    fun getRevision(): Int = revision
}
