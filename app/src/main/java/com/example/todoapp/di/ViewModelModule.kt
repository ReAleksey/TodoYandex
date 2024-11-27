package com.example.todoapp.di


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.ui.view.viewmodel.EditTodoItemViewModel
import com.example.todoapp.ui.view.viewmodel.TodoListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TodoListViewModel::class)
    abstract fun bindTodoListViewModel(viewModel: TodoListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditTodoItemViewModel::class)
    abstract fun bindEditTodoItemViewModel(viewModel: EditTodoItemViewModel): ViewModel
}