package com.asterisk.mydoings.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {

    fun getAllTodo(): Flow<List<Todo>> = todoDao.getAllTodo()
}