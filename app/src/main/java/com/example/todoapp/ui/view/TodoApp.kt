package com.example.todoapp.ui.view

import android.app.Application
import com.example.todoapp.data.repository.TodoItemRepository
import com.example.todoapp.data.repository.UserPreferencesRepositoryInterface
import com.example.todoapp.di.AppComponent
import com.example.todoapp.di.DaggerAppComponent
import javax.inject.Inject

class TodoApp : Application() {

    @Inject
    lateinit var todoItemRepository: TodoItemRepository

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepositoryInterface

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)
    }
}