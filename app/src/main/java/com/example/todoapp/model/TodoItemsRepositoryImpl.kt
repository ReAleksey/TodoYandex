package com.example.todoapp.model

import com.example.todoapp.network.ImportanceNetwork
import com.example.todoapp.network.NetworkModule
import com.example.todoapp.network.TodoItemNetwork
import com.example.todoapp.network.TodoItemRequest
import com.example.todoapp.network.TodoListRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.util.Date

class TodoItemsRepositoryImpl : TodoItemRepository {

    private val _itemsFlow = MutableStateFlow<List<TodoItem>>(emptyList())
    private val mutex = Mutex()
    private var revision: Int = 0

    override fun getItemsFlow(): StateFlow<List<TodoItem>> = _itemsFlow.asStateFlow()

    override suspend fun getItem(id: String): TodoItem? =
        _itemsFlow.value.firstOrNull { it.id == id }

    override suspend fun addItem(item: TodoItem) {
        mutex.withLock {
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
                // Обработка ошибок
                throw Exception("Failed to add item")
            }
        }
    }

    override suspend fun saveItem(item: TodoItem) {
        mutex.withLock {
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
                // Обработка ошибок
                throw Exception("Failed to save item")
            }
        }
    }

    override suspend fun deleteItem(item: TodoItem) {
        mutex.withLock {
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
                // Обработка ошибок
                throw Exception("Failed to delete item")
            }
        }
    }

    override suspend fun synchronize() {
        mutex.withLock {
            val response = NetworkModule.apiService.getTodoList()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    revision = body.revision
                    val items = body.list.map { it.toDomainModel() }
                    _itemsFlow.emit(items)
                }
            } else {
                // Обработка ошибок
                throw Exception("Failed to synchronize")
            }
        }
    }

    // Дополнительные функции для преобразования моделей
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
            lastUpdatedBy = "your_device_id" // Замените на актуальное значение
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
}
