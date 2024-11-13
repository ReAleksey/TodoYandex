package com.example.todoapp.ui.view.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.ui.view.TodoApp
import com.example.todoapp.model.TodoItem
import com.example.todoapp.data.repository.TodoItemRepository
import com.example.todoapp.data.repository.UserPreferences
import com.example.todoapp.data.repository.UserPreferencesRepositoryInterface
import com.example.todoapp.utils.TodoListEvent
import com.example.todoapp.utils.TodoListUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date


class TodoListViewModel(
    application: Application,
    private val todoItemRepository: TodoItemRepository,
    private val userPreferencesRepository: UserPreferencesRepositoryInterface
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<TodoListUiState>(TodoListUiState.Loading)
    val uiState: StateFlow<TodoListUiState> = _uiState.asStateFlow()

    private val filterFlow = MutableStateFlow(TodoListUiState.FilterState.NOT_COMPLETED)
    private val _eventFlow = MutableSharedFlow<TodoListEvent>()
    val eventFlow = _eventFlow.asSharedFlow()
    private val connectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val itemsFlow: StateFlow<List<TodoItem>> = todoItemRepository.getItemsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesRepository.userPreferencesFlow


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    todoItemRepository.synchronize()
                } catch (e: Exception) {
                    Log.e("TodoListViewModel", "Error in networkCallback", e)
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            combine(
            todoItemRepository.getItemsFlow(),
                userPreferencesFlow
            ) { items, preferences ->
                val filterState = if (preferences.showCompleted) {
                    TodoListUiState.FilterState.ALL
                } else {
                    TodoListUiState.FilterState.NOT_COMPLETED
                }
                TodoListUiState.Loaded(
                    items = items.filter(filterState.filter),
                    filterState = filterState,
                    doneCount = items.count { it.isCompleted }
                )
            }
                .catch { e ->
                    _uiState.value = TodoListUiState.Error(e.message ?: "Unknown error")
                }
                .collect {
                    _uiState.value = it
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                todoItemRepository.synchronize()
            } catch (e: Exception) {
                Log.d("MyLog", "TodoListViewModel: Error in init: ", e)
                _uiState.value = TodoListUiState.Error(e.message ?: "Unknown error")
            }
        }
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
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
                Log.e("MyLog", "TodoListViewModel: Error in onChecked", e)
                _eventFlow.emit(TodoListEvent.ShowSnackbar("Failed to update item"))
            }
        }
    }

    fun delete(item: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                todoItemRepository.deleteItem(item)
            } catch (e: Exception) {
                Log.e("MyLog", "TodoListViewModel: Error in delete: ", e)
                _eventFlow.emit(TodoListEvent.ShowSnackbar("Failed to delete item"))
            }
        }
    }

    fun onFilterChange(filterState: TodoListUiState.FilterState) {
        viewModelScope.launch {
            filterFlow.emit(filterState)
        }
    }

    fun getLastKnownItems(): List<TodoItem> {
        return when (val currentState = uiState.value) {
            is TodoListUiState.Loaded -> currentState.items
            else -> emptyList()
        }
    }

    fun retryFetchingData() {
        viewModelScope.launch {
            _uiState.value = TodoListUiState.Loading
            try {
                todoItemRepository.synchronize()
            } catch (e: Exception) {
                Log.e("MyLog", "TodoListViewModel: Error in retryFetchingData", e)
                _uiState.value = TodoListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    fun useOfflineMode() {
        viewModelScope.launch {
            _uiState.value = TodoListUiState.Loaded(
                items = todoItemRepository.getItemsFlow().first(),
                filterState = filterFlow.value,
                doneCount = todoItemRepository.getItemsFlow().first().count { it.isCompleted }
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                val todoItemRepository =
                    (application as TodoApp).todoItemRepository
                val userPreferencesRepository =
                    application.userPreferencesRepository
                TodoListViewModel(
                    application = application,
                    todoItemRepository = todoItemRepository,
                    userPreferencesRepository = userPreferencesRepository
                )
            }
        }
    }

    fun updateDarkTheme(darkTheme: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateDarkTheme(darkTheme)
        }
    }

    fun updateShowCompleted(showCompleted: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateShowCompleted(showCompleted)
            val filterState = if (showCompleted) {
                TodoListUiState.FilterState.ALL
            } else {
                TodoListUiState.FilterState.NOT_COMPLETED
            }
            onFilterChange(filterState)
        }
    }

    suspend fun getInitialPreferences(): UserPreferences {
        return userPreferencesRepository.getInitialPreferences()
    }
}
