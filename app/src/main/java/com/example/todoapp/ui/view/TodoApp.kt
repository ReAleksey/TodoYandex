package com.example.todoapp.ui.view

import android.app.Application
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapp.data.TodoDatabase
import com.example.todoapp.data.repository.TodoItemRepository
import com.example.todoapp.data.repository.TodoItemsRepositoryImpl
import com.example.todoapp.data.repository.UserPreferencesRepository
import com.example.todoapp.data.worker.SynchronizeWorker
import java.util.concurrent.TimeUnit

class TodoApp: Application() {

    lateinit var database: TodoDatabase
        private set
    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    val todoItemRepository: TodoItemRepository by lazy {
        TodoItemsRepositoryImpl(
            context = this,
            database = database
        )
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo_database"
        ).build()
        userPreferencesRepository = UserPreferencesRepository(this)
        schedulePeriodicSynchronization()
    }

    private fun schedulePeriodicSynchronization() {
        val workRequest = PeriodicWorkRequestBuilder<SynchronizeWorker>(8, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "synchronize_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}