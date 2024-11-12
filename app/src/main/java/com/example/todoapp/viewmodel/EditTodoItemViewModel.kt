package com.example.todoapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApp
import com.example.todoapp.model.TodoItem
import com.example.todoapp.model.TodoItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

class EditTodoItemViewModel(
    private val todoItemRepository: TodoItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditTodoItemUiState>(EditTodoItemUiState.Loading)
    val uiState: StateFlow<EditTodoItemUiState> = _uiState
    private val _eventFlow = MutableSharedFlow<EditTodoItemEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var job: Job? = null

    fun setItem(itemId: String?) {
        job?.cancel()
        job = viewModelScope.launch {
            viewModelScope.launch {
                try {
                    val item = withContext(Dispatchers.IO) {
                        itemId?.let { todoItemRepository.getItem(itemId) }
                    }
                    _uiState.emit(
                        if (item == null)
                            EditTodoItemUiState.Loaded(
                                TodoItem(
                                    id = UUID.randomUUID().toString(),
                                    text = ""
                                ),
                                EditTodoItemUiState.ItemState.NEW
                            )
                        else
                            EditTodoItemUiState.Loaded(
                                item,
                                EditTodoItemUiState.ItemState.EDIT
                            )
                    )
                } catch (e: Exception) {
                    _uiState.emit(EditTodoItemUiState.Error(e))
                }
            }
        }
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (uiState.value is EditTodoItemUiState.Loaded) {
                    val state = uiState.value as EditTodoItemUiState.Loaded
                    when (state.itemState) {
                        EditTodoItemUiState.ItemState.EDIT -> {
                            val updatedItem = state.item.copy(
                                modifiedAt = Date()
                            )
                            todoItemRepository.saveItem(updatedItem)
                        }

                        EditTodoItemUiState.ItemState.NEW -> {
                            val newItem = state.item.copy(
                                createdAt = Date(),
                                modifiedAt = Date()
                            )
                            todoItemRepository.addItem(newItem)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MyLog", "EditTodoItemViewModel: Error in save", e)
                _eventFlow.emit(EditTodoItemEvent.ShowSnackbar("Failed to save item"))
            }
        }
    }

    fun edit(item: TodoItem) {
        if (uiState.value is EditTodoItemUiState.Loaded) {
            _uiState.update {
                val state = it as EditTodoItemUiState.Loaded
                state.copy(
                    item = item
                )
            }
        }
    }

    fun delete() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (uiState.value is EditTodoItemUiState.Loaded) {
                    val item = (uiState.value as EditTodoItemUiState.Loaded).item
                    todoItemRepository.deleteItem(item)
                }
            } catch (e: Exception) {
                Log.e("MyLog", "EditTodoItemViewModel: Error in delete", e)
                _eventFlow.emit(EditTodoItemEvent.ShowSnackbar("Failed to delete item"))
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (checkNotNull(this[APPLICATION_KEY]) as TodoApp)
                val todoItemRepository = application.todoItemRepository
                EditTodoItemViewModel(
                    todoItemRepository = todoItemRepository
                )
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}

sealed class EditTodoItemUiState {
    data object Loading : EditTodoItemUiState()
    data class Error(val exception: Throwable) : EditTodoItemUiState()
    data class Loaded(
        val item: TodoItem,
        val itemState: ItemState
    ) : EditTodoItemUiState()

    enum class ItemState {
        NEW,
        EDIT
    }
}