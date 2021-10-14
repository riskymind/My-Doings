package com.asterisk.mydoings.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: Todo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    fun getAllTodo(searchQuery: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Todo>> =
        when(sortOrder) {
            SortOrder.BY_NAME -> getTodoSortedByName(searchQuery, hideCompleted)
            SortOrder.BY_DATE -> getTodoSortedByDate(searchQuery, hideCompleted)
        }

    @Query("SELECT * FROM todo_table WHERE (completed != :hideCompleted OR completed = 0) AND whatTodo LIKE '%' || :searchQuery || '%' ORDER BY important DESC, whatTodo")
    fun getTodoSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Todo>>

    @Query("SELECT * FROM todo_table WHERE (completed != :hideCompleted OR completed = 0) AND whatTodo LIKE '%' || :searchQuery || '%' ORDER BY important DESC, createdAt")
    fun getTodoSortedByDate(searchQuery: String, hideCompleted: Boolean): Flow<List<Todo>>
}