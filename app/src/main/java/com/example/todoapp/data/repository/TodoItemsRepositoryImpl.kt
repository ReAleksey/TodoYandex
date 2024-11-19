package com.example.todoapp.data.repository

import com.example.todoapp.utils.toDomainModel
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.example.todoapp.data.local.LocalDataSource
import com.example.todoapp.data.network.NetworkModule
import com.example.todoapp.data.remote.RemoteDataSource
import com.example.todoapp.model.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class TodoItemsRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : TodoItemRepository {

    private val mutex = Mutex()

    override fun getItemsFlow(): Flow<List<TodoItem>> = localDataSource.getAllItems()

    override suspend fun getItem(id: String): TodoItem? = localDataSource.getItemById(id)

    override suspend fun addItem(item: TodoItem) {
        localDataSource.insertItem(item)
        try {
            remoteDataSource.addTodoItem(item)
        } catch (e: Exception) {
            Log.e("MyLog", "TodoItemsRepositoryImpl: Error in addItem", e)
        }
    }

    override suspend fun saveItem(item: TodoItem) {
        localDataSource.updateItem(item)
        try {
            remoteDataSource.updateTodoItem(item)
        } catch (e: Exception) {
            Log.e("MyLog", "TodoItemsRepositoryImpl: Error in saveItem", e)
        }
    }

    override suspend fun deleteItem(item: TodoItem) {
        localDataSource.deleteItem(item)
        try {
            remoteDataSource.deleteTodoItem(item.id)
        } catch (e: Exception) {
            Log.e("MyLog", "TodoItemsRepositoryImpl: Error in deleteItem", e)
        }
    }

    override suspend fun synchronize() {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                executeWithRetry {
                    val serverResponse = remoteDataSource.getTodoList()
                    val serverItems = serverResponse.list.map { it.toDomainModel() }

                    val localItems = localDataSource.getCurrentItems()

                    val mergedItems = mergeData(localItems, serverItems)

                    localDataSource.deleteAllItems()
                    localDataSource.insertItems(mergedItems)

                    remoteDataSource.updateTodoList(mergedItems)
                }
            }
        }
    }

    private fun mergeData(
        localItems: List<TodoItem>,
        serverItems: List<TodoItem>
    ): List<TodoItem> {
        val localItemsMap = localItems.associateBy { it.id }
        val serverItemsMap = serverItems.associateBy { it.id }

        val mergedMap = mutableMapOf<String, TodoItem>()

        val allIds = localItemsMap.keys + serverItemsMap.keys
        for (id in allIds) {
            val localItem = localItemsMap[id]
            val serverItem = serverItemsMap[id]

            if (localItem != null && serverItem != null) {
                val localModifiedAt = localItem.modifiedAt ?: localItem.createdAt
                val serverModifiedAt = serverItem.modifiedAt ?: serverItem.createdAt

                if (localModifiedAt.after(serverModifiedAt)) {
                    mergedMap[id] = localItem
                } else {
                    mergedMap[id] = serverItem
                }
            } else if (localItem != null) {
                mergedMap[id] = localItem
            } else if (serverItem != null) {
                mergedMap[id] = serverItem
            }
        }

        return mergedMap.values.toList()
    }

    private suspend fun <T> executeWithRetry(
        maxRetries: Int = 3,
        initialDelayMillis: Long = 1000,
        maxDelayMillis: Long = 5000,
        action: suspend () -> T
    ): T {
        var currentDelay = initialDelayMillis
        repeat(maxRetries) { attempt ->
            try {
                return action()
            } catch (e: Exception) {
                if (attempt == maxRetries - 1) {
                    throw e
                }
                delay(currentDelay)
                currentDelay = (currentDelay * 2).coerceAtMost(maxDelayMillis)
            }
        }
        throw Exception("Failed after $maxRetries retries")
    }
}