package com.example.todoapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApp
import com.example.todoapp.model.TodoItem
import com.example.todoapp.model.TodoItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import kotlinx.coroutines.flow.combine


class TodoListViewModel(
    private val todoItemRepository: TodoItemRepository
) : ViewModel() {

    // Define filterFlow
    private val filterFlow = MutableStateFlow(TodoListUiState.FilterState.NOT_COMPLETED)

    val uiState: StateFlow<TodoListUiState> = combine(
        todoItemRepository.getItemsFlow(),
        filterFlow
    ) { items, filter ->
        TodoListUiState.Loaded(
            items = items.filter(filter.filter),
            filterState = filter,
            doneCount = items.count { it.isCompleted }
        ) as TodoListUiState
    }
        .catch { e ->
            emit(TodoListUiState.Error(e))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TodoListUiState.Loading
        )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                todoItemRepository.synchronize()
            } catch (e: Exception) {
                Log.d("MyLog", "TodoListViewModel: Error in init: ", e)
            }
        }
    }

    fun onChecked(item: TodoItem, checked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedItem = item.copy(
                    isCompleted = checked,
                    modifiedAt = Date()
                )
                todoItemRepository.saveItem(updatedItem)
            } catch (e: Exception) {
                Log.e("MyLog", "TodoListViewModel: Error in onChecked: ", e)
            }
        }
    }

    fun delete(item: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                todoItemRepository.deleteItem(item)
            } catch (e: Exception) {
                Log.e("MyLog", "TodoListViewModel: Error in delete: ", e)
            }
        }
    }

    fun onFilterChange(filterState: TodoListUiState.FilterState) {
        viewModelScope.launch(Dispatchers.IO) {
            filterFlow.emit(filterState)
        }
    }

    fun getLastKnownItems(): List<TodoItem> {
        return when (val currentState = uiState.value) {
            is TodoListUiState.Loaded -> currentState.items
            else -> emptyList()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val todoItemRepository =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApp).todoItemRepository
                TodoListViewModel(
                    todoItemRepository = todoItemRepository
                )
            }
        }
    }
}