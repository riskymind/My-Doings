package com.asterisk.mydoings.ui.todo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.asterisk.mydoings.data.TodoRepository

class TodoFragmentViewModel @ViewModelInject constructor(
    private val todoRepository: TodoRepository
): ViewModel() {


    val allTodo = todoRepository.getAllTodo().asLiveData()
}