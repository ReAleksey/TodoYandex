package com.example.todoapp.data

import com.example.todoapp.data.local.RevisionStorage
import com.example.todoapp.data.network.TodoApiService
import com.example.todoapp.data.network.TodoItemRequest
import com.example.todoapp.data.network.TodoListRequest
import com.example.todoapp.data.network.TodoListResponse
import com.example.todoapp.model.TodoItem
import com.example.todoapp.utils.toNetworkModel
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiService: TodoApiService,
    private val deviceId: String,
    private val revisionStorage: RevisionStorage
) {

    suspend fun getTodoList(): TodoListResponse {
        val response = apiService.getTodoList()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                revisionStorage.revision = body.revision
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
        val response = apiService.updateTodoList(revisionStorage.revision, request)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                revisionStorage.revision = body.revision
            } else {
                throw Exception("Empty response body")
            }
        } else {
            handleErrorResponse(response)
        }
    }

    suspend fun addTodoItem(item: TodoItem) {
        val request = TodoItemRequest(item.toNetworkModel(deviceId))
        val response = apiService.addTodoItem(revisionStorage.revision, request)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                revisionStorage.revision = body.revision
            } else {
                throw Exception("Empty response body")
            }
        } else {
            handleErrorResponse(response)
        }
    }

    suspend fun updateTodoItem(item: TodoItem) {
        val request = TodoItemRequest(item.toNetworkModel(deviceId))
        val response = apiService.updateTodoItem(revisionStorage.revision, item.id, request)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                revisionStorage.revision = body.revision
            } else {
                throw Exception("Empty response body")
            }
        } else {
            handleErrorResponse(response)
        }
    }

    suspend fun deleteTodoItem(itemId: String) {
        val response = apiService.deleteTodoItem(revisionStorage.revision, itemId)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                revisionStorage.revision = body.revision
            } else {
                throw Exception("Empty response body")
            }
        } else {
            throw Exception("Failed to delete item: ${response.code()}")
        }
    }

    private fun handleErrorResponse(response: Response<*>) {
        when (response.code()) {
            400 -> throw Exception("Invalid request")
            401 -> throw Exception("Unauthorized")
            404 -> throw Exception("Not found")
            409 -> throw Exception("Conflict: Revision mismatch")
            else -> throw Exception("Unknown error: ${response.code()}")
        }
    }
}
