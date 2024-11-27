package com.example.todoapp.data.repository

import androidx.datastore.preferences.core.edit
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor (
    private val context: Context
) : UserPreferencesRepositoryInterface {

    private val Context.dataStore by preferencesDataStore(name = "user_preferences")

    private object PreferencesKeys {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        val SHOW_COMPLETED_KEY = booleanPreferencesKey("show_completed")
    }

    override val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            val darkTheme = preferences[PreferencesKeys.DARK_THEME_KEY] ?: false
            val showCompleted = preferences[PreferencesKeys.SHOW_COMPLETED_KEY] ?: true
            UserPreferences(darkTheme, showCompleted)
        }

    override suspend fun updateDarkTheme(darkTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_THEME_KEY] = darkTheme
        }
    }

    override suspend fun updateShowCompleted(showCompleted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_COMPLETED_KEY] = showCompleted
        }
    }

    override suspend fun getInitialPreferences(): UserPreferences {
        val preferences = context.dataStore.data.first()
        val darkTheme = preferences[PreferencesKeys.DARK_THEME_KEY] ?: false
        val showCompleted = preferences[PreferencesKeys.SHOW_COMPLETED_KEY] ?: true
        return UserPreferences(darkTheme, showCompleted)
    }
}

data class UserPreferences(
    val darkTheme: Boolean,
    val showCompleted: Boolean
)
