package com.example.todoapp

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapp.model.TodoItemRepository
import com.example.todoapp.model.TodoItemsRepositoryImpl
import com.example.todoapp.network.SynchronizeWorker
import java.util.concurrent.TimeUnit

class TodoApp: Application() {
    val todoItemRepository: TodoItemRepository by lazy { TodoItemsRepositoryImpl(this) }
    override fun onCreate() {
        super.onCreate()
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