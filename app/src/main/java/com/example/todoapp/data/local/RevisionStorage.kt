package com.example.todoapp.data.local

import android.content.Context
import androidx.core.content.edit

class RevisionStorage(context: Context) {

    companion object {
        private const val PREFS_NAME = "revision_prefs"
        private const val REVISION_KEY = "revision_key"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var revision: Int
        get() = sharedPreferences.getInt(REVISION_KEY, 0)
        set(value) = sharedPreferences.edit { putInt(REVISION_KEY, value) }
}