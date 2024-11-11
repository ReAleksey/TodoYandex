package com.example.todoapp.network

import retrofit2.Response
import retrofit2.http.*

interface TodoApiService {

    @GET("list")
    suspend fun getTodoList(): Response<TodoListResponse>

    @PATCH("list")
    suspend fun updateTodoList(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TodoListRequest
    ): Response<TodoListResponse>

    @GET("list/{id}")
    suspend fun getTodoItem(
        @Path("id") id: String
    ): Response<TodoItemResponse>

    @POST("list")
    suspend fun addTodoItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TodoItemRequest
    ): Response<TodoItemResponse>

    @PUT("list/{id}")
    suspend fun updateTodoItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String,
        @Body request: TodoItemRequest
    ): Response<TodoItemResponse>

    @DELETE("list/{id}")
    suspend fun deleteTodoItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String
    ): Response<TodoItemResponse>
}