package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApp
import com.example.todoapp.model.TodoItem
import com.example.todoapp.model.TodoItemRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class EditTodoItemViewModel(
    private val todoItemRepository: TodoItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditTodoItemUiState>(EditTodoItemUiState.Loading)
    val uiState: StateFlow<EditTodoItemUiState> = _uiState

    private var job: Job? = null

    fun setItem(itemId: String?) {
        job?.cancel()
        job = viewModelScope.launch {
            viewModelScope.launch {
                try {
                    val item = itemId?.let { todoItemRepository.getItem(itemId) }
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
        viewModelScope.launch {
            if (uiState.value is EditTodoItemUiState.Loaded) {
                val state = (uiState.value as EditTodoItemUiState.Loaded)
                when (state.itemState) {
                    EditTodoItemUiState.ItemState.EDIT -> todoItemRepository.saveItem(item = state.item)
                    EditTodoItemUiState.ItemState.NEW -> todoItemRepository.addItem(item = state.item)
                }
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
        require(uiState.value is EditTodoItemUiState.Loaded)
        viewModelScope.launch {
            todoItemRepository.deleteItem((uiState.value as EditTodoItemUiState.Loaded).item)
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val todoItemRepository =
                    (this[APPLICATION_KEY] as TodoApp).todoItemRepository
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