package com.example.todoapp.data.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepositoryInterface {
    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun updateDarkTheme(darkTheme: Boolean)
    suspend fun updateShowCompleted(showCompleted: Boolean)
    suspend fun getInitialPreferences(): UserPreferences
}
