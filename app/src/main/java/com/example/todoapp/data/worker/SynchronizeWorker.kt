package com.example.todoapp.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapp.ui.view.TodoApp

class SynchronizeWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val todoItemRepository = (applicationContext as TodoApp).todoItemRepository
        return try {
            todoItemRepository.synchronize()
            Result.success()
        } catch (e: Exception) {
            Log.e("MyLog", "SynchronizeWorker: Error in doWork", e)
            Result.retry()
        }
    }
}
