package com.example.todoapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todoapp.model.TodoItem
import com.example.todoapp.model.TodoItemDao
import com.example.todoapp.utils.Converters

@Database(entities = [TodoItem::class], version = 1)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoItemDao(): TodoItemDao
}