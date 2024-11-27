package com.example.todoapp.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {

    @Query("SELECT * FROM todo_items")
    fun getAllItems(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    suspend fun getItemById(id: String): TodoItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: TodoItem): Long

    @Update
    suspend fun updateItem(item: TodoItem): Int

    @Delete
    suspend fun deleteItem(item: TodoItem): Int

    @Query("DELETE FROM todo_items")
    suspend fun deleteAllItems(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<TodoItem>): List<Long>
}

