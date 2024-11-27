package com.example.todoapp.di

import com.example.todoapp.data.RemoteDataSource
import com.example.todoapp.data.local.LocalDataSource
import com.example.todoapp.data.local.RevisionStorage
import com.example.todoapp.data.network.TodoApiService
import com.example.todoapp.data.repository.TodoItemRepository
import com.example.todoapp.data.repository.TodoItemsRepositoryImpl
import com.example.todoapp.data.repository.UserPreferencesRepository
import com.example.todoapp.data.repository.UserPreferencesRepositoryInterface
import com.example.todoapp.model.TodoItemDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import java.util.UUID
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTodoItemRepository(
        impl: TodoItemsRepositoryImpl
    ): TodoItemRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepository
    ): UserPreferencesRepositoryInterface

    companion object {

        @Provides
        @Singleton
        fun provideLocalDataSource(todoItemDao: TodoItemDao): LocalDataSource {
            return LocalDataSource(todoItemDao)
        }

        @Provides
        @Singleton
        fun provideRemoteDataSource(
            apiService: TodoApiService,
            revisionStorage: RevisionStorage
        ): RemoteDataSource {
            val deviceId = UUID.randomUUID().toString()
            return RemoteDataSource(apiService, deviceId, revisionStorage)
        }
    }
}