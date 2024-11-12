package com.example.todoapp.model

import android.content.Context
import android.util.Log
import com.example.todoapp.network.ImportanceNetwork
import com.example.todoapp.network.NetworkModule
import com.example.todoapp.network.TodoItemNetwork
import com.example.todoapp.network.TodoItemRequest
import com.example.todoapp.network.TodoListRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.util.Date
import android.provider.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoItemsRepositoryImpl(private val context: Context) : TodoItemRepository {

    private val deviceId: String = getDeviceId(context)
    private val _itemsFlow = MutableStateFlow<List<TodoItem>>(emptyList())
    private val mutex = Mutex()
    private var revision: Int = 0

    override fun getItemsFlow(): StateFlow<List<TodoItem>> = _itemsFlow.asStateFlow()

    override suspend fun getItem(id: String): TodoItem? =
        _itemsFlow.value.firstOrNull { it.id == id }

    private suspend fun <T> executeWithRetry(
        maxRetries: Int = 3,
        initialDelayMillis: Long = 1000,
        maxDelayMillis: Long = 5000,
        action: suspend () -> T
    ): T {
        var currentDelay = initialDelayMillis
        repeat(maxRetries) { attempt ->
            try {
                Log.d("MyLog", "executeWithRetry(): Попытка ${attempt + 1}")
                return action()
            } catch (e: Exception) {
                if (attempt == maxRetries - 1) {
                    Log.e("MyLog", "executeWithRetry(): Достигнуто максимальное количество попыток")
                    throw e
                }
                Log.e("MyLog", "executeWithRetry(): Ошибка, повтор через $currentDelay мс", e)
                delay(currentDelay)
                currentDelay = (currentDelay * 2).coerceAtMost(maxDelayMillis)
            }
        }
        throw Exception("Failed after $maxRetries retries")
    }

    override suspend fun addItem(item: TodoItem) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                executeWithRetry {
                    val response = NetworkModule.apiService.addTodoItem(
                        revision,
                        TodoItemRequest(item.toNetworkModel())
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            revision = body.revision
                            synchronize()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(
                            "MyLog",
                            "TodoItemsRepositoryImpl: Failed to add item: ${response.code()} ${response.message()} $errorBody"
                        )
                        throw Exception("Failed to add item: ${response.code()} ${response.message()}")
                    }
                }
            }
        }
    }

    override suspend fun saveItem(item: TodoItem) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                executeWithRetry {
                    val response = NetworkModule.apiService.updateTodoItem(
                        revision,
                        item.id,
                        TodoItemRequest(item.toNetworkModel())
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            revision = body.revision
                            synchronize()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(
                            "MyLog",
                            "TodoItemsRepositoryImpl: Failed to save item: ${response.code()} ${response.message()} $errorBody"
                        )
                        throw Exception("Failed to save item: ${response.code()} ${response.message()}")
                    }
                }
            }
        }
    }

    override suspend fun deleteItem(item: TodoItem) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                executeWithRetry {
                    val response = NetworkModule.apiService.deleteTodoItem(
                        revision,
                        item.id
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            revision = body.revision
                            synchronize()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(
                            "MyLog",
                            "TodoItemsRepositoryImpl: Error in deleteItem: ${response.code()} ${response.message()} $errorBody"
                        )
                        throw Exception("Failed to delete item: ${response.code()} ${response.message()}")
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
                            val items = body.list.map { it.toDomainModel() }
                            _itemsFlow.value = items
                            Log.d("MyLog", "synchronize(): Данные успешно обновлены")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(
                            "MyLog",
                            "TodoItemsRepositoryImpl: Failed to synchronize: ${response.code()} ${response.message()} $errorBody"
                        )
                        throw Exception("Failed to synchronize: ${response.code()} ${response.message()}")
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
    private fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

}

