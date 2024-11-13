package com.example.todoapp.data.repository

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.example.todoapp.data.TodoDatabase
import com.example.todoapp.data.network.ImportanceNetwork
import com.example.todoapp.data.network.NetworkModule
import com.example.todoapp.data.network.TodoItemNetwork
import com.example.todoapp.data.network.TodoListRequest
import com.example.todoapp.model.TodoImportance
import com.example.todoapp.model.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Date

class TodoItemsRepositoryImpl(
    private val context: Context,
    private val database: TodoDatabase
) : TodoItemRepository {

    private val todoItemDao = database.todoItemDao()
    private val deviceId: String = getDeviceId(context)
    private val _itemsFlow = MutableStateFlow<List<TodoItem>>(emptyList())
    private val mutex = Mutex()
    private var revision: Int = 0

    override fun getItemsFlow(): Flow<List<TodoItem>> {
        return todoItemDao.getAllItems()
    }

    override suspend fun getItem(id: String): TodoItem? {
        return todoItemDao.getItemById(id)
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
                Log.d("MyLog", "TodoItemsRepositoryImpl: executeWithRetry(): Попытка ${attempt + 1}")
                return action()
            } catch (e: Exception) {
                if (attempt == maxRetries - 1) {
                    Log.e("MyLog", "TodoItemsRepositoryImpl: executeWithRetry(): Достигнуто максимальное количество попыток")
                    throw e
                }
                Log.e("MyLog", "TodoItemsRepositoryImpl: executeWithRetry(): Ошибка, повтор через $currentDelay мс", e)
                delay(currentDelay)
                currentDelay = (currentDelay * 2).coerceAtMost(maxDelayMillis)
            }
        }
        throw Exception("Failed after $maxRetries retries")
    }

    override suspend fun addItem(item: TodoItem) {
    todoItemDao.insertItem(item)
    synchronize()
    }

    override suspend fun saveItem(item: TodoItem) {
    todoItemDao.updateItem(item)
    synchronize()
    }

    override suspend fun deleteItem(item: TodoItem) {
        withContext(Dispatchers.IO) {
            todoItemDao.deleteItem(item)
            executeWithRetry {
                Log.d("MyLog", "TodoItemsRepositoryImpl: deleteItem(): Отправка DELETE запроса на сервер для задачи ${item.id}")
                val response = NetworkModule.apiService.deleteTodoItem(revision, item.id)
                when {
                    response.isSuccessful -> {
                        val body = response.body()
                        if (body != null) {
                            revision = body.revision
                        } else {
                            Log.e("MyLog", "deleteItem(): Сервер вернул пустой ответ")
                        }
                    }
                    response.code() == 400 -> {
                        synchronize()
                        deleteItem(item)
                    }
                    response.code() == 404 -> {
                        Log.e("MyLog", "deleteItem(): Элемент не найден на сервере, считаем удалённым")
                    }
                    else -> {
                        throw Exception("Failed to delete item on server: ${response.code()}")
                    }
                }
            }
        }
    }



    override suspend fun synchronize() {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                executeWithRetry {
                    Log.d("MyLog", "synchronize(): Начало запроса на сервер")
                    val response = NetworkModule.apiService.getTodoList()
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            revision = body.revision
                            val serverItems = body.list.map { it.toDomainModel() }

                            val localItems = todoItemDao.getAllItems().first()
                            val mergedItems = mergeData(localItems, serverItems)

                            todoItemDao.deleteAllItems()
                            todoItemDao.insertItems(mergedItems)

                            val request = TodoListRequest(mergedItems.map { it.toNetworkModel() })
                            val updateResponse = NetworkModule.apiService.updateTodoList(revision, request)
                            if (updateResponse.isSuccessful) {
                                val updateBody = updateResponse.body()
                                if (updateBody != null) {
                                    revision = updateBody.revision
                                }
                            } else {
                                throw Exception("Failed to update server data")
                            }
                        }
                    } else {
                        throw Exception("Failed to fetch data from server")
                    }
                }
            }
        }
    }



    private fun TodoItem.toNetworkModel(): TodoItemNetwork {
        return TodoItemNetwork(
            id = id,
            text = text,
            importance = importance.toNetworkImportance(),
            deadline = deadline?.time?.div(1000),
            isCompleted = isCompleted,
            color = null,
            createdAt = createdAt.time.div(1000),
            changedAt = modifiedAt?.time?.div(1000) ?: createdAt.time.div(1000),
            lastUpdatedBy = deviceId
        )
    }

    private fun TodoItemNetwork.toDomainModel(): TodoItem {
        return TodoItem(
            id = id,
            text = text,
            importance = importance.toDomainImportance(),
            deadline = deadline?.let { Date(it * 1000) },
            isCompleted = isCompleted,
            createdAt = Date(createdAt * 1000),
            modifiedAt = Date(changedAt * 1000)
        )
    }

    private fun TodoImportance.toNetworkImportance(): ImportanceNetwork {
        return when (this) {
            TodoImportance.LOW -> ImportanceNetwork.LOW
            TodoImportance.DEFAULT -> ImportanceNetwork.BASIC
            TodoImportance.HIGH -> ImportanceNetwork.IMPORTANT
        }
    }

    private fun ImportanceNetwork.toDomainImportance(): TodoImportance {
        return when (this) {
            ImportanceNetwork.LOW -> TodoImportance.LOW
            ImportanceNetwork.BASIC -> TodoImportance.DEFAULT
            ImportanceNetwork.IMPORTANT -> TodoImportance.HIGH
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



    private fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

}
