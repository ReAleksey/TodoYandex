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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoListViewModel(
    private val todoItemRepository: TodoItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TodoListUiState>(TodoListUiState.Loading)
    val uiState: StateFlow<TodoListUiState> = _uiState

    // Define filterFlow
    private val filterFlow = MutableStateFlow(TodoListUiState.FilterState.NOT_COMPLETED)

    init {
        viewModelScope.launch {
            try {
                // Synchronize data with the server
                todoItemRepository.synchronize()

                // Combine itemsFlow and filterFlow
                todoItemRepository.getItemsFlow()
                    .combine(filterFlow) { items, filter ->
                        TodoListUiState.Loaded(
                            items = items.filter(filter.filter),
                            filterState = filter,
                            doneCount = items.count { it.isCompleted }
                        )
                    }
                    .catch { e ->
                        _uiState.value = TodoListUiState.Error(e)
                    }
                    .collect { uiStateValue ->
                        _uiState.value = uiStateValue
                    }
            } catch (e: Exception) {
                _uiState.value = TodoListUiState.Error(e)
            }
        }
    }

    fun onChecked(item: TodoItem, checked: Boolean) {
        viewModelScope.launch {
            if (uiState.value is TodoListUiState.Loaded) {
                val newItem = item.copy(isCompleted = checked)
                todoItemRepository.saveItem(newItem)
            }
        }
    }

    fun delete(item: TodoItem) {
        viewModelScope.launch {
            todoItemRepository.deleteItem(item)
        }
    }

    fun onFilterChange(filterState: TodoListUiState.FilterState) {
        viewModelScope.launch {
            filterFlow.emit(filterState)
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