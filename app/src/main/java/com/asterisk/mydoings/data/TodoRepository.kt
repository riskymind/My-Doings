package com.asterisk.mydoings.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {

    fun getAllTodo(
        searchQuery: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean
    ): Flow<List<Todo>> = todoDao.getAllTodo(searchQuery, sortOrder, hideCompleted)

    suspend fun updateTodo(todo: Todo) = todoDao.updateTodo(todo)

    suspend fun deleteTodo(todo: Todo) = todoDao.deleteTodo(todo)

    suspend fun insertTodo(todo: Todo) = todoDao.insertTodo(todo)
}