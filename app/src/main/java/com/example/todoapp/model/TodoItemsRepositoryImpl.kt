package com.example.todoapp.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date

class TodoItemsRepositoryImpl : TodoItemRepository {

    private val _itemsFlow = MutableStateFlow<List<TodoItem>>(emptyList())

    override fun getItemsFlow(): StateFlow<List<TodoItem>> = _itemsFlow.asStateFlow()

    override suspend fun getItem(id: String): TodoItem? =
        _itemsFlow.value.firstOrNull { it.id == id }

    override suspend fun addItem(item: TodoItem) {
        _itemsFlow.update { state ->
            state + listOf(
                item.copy(
                    id = ((state.maxOfOrNull { item -> item.id.toLong() }
                        ?: 0L) + 1L).toString(),
                    createdAt = Date()
                )
            )
        }
    }

    override suspend fun saveItem(item: TodoItem) {
        _itemsFlow.update { state ->
            state.map {
                if (it.id == item.id)
                    item.copy(modifiedAt = Date())
                else
                    it
            }
        }
    }

    override suspend fun deleteItem(item: TodoItem) {
        _itemsFlow.update { state -> state.filter { it.id != item.id } }
    }

}