package com.asterisk.mydoings.ui.deleteTodos

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asterisk.mydoings.data.TodoDao
import kotlinx.coroutines.launch

class TodoDeleteDialogViewModel @ViewModelInject constructor(
    private val todoDao: TodoDao
): ViewModel() {

    fun yesDeleteTodo() = viewModelScope.launch {
        todoDao.deleteAllCompleted()
    }

}