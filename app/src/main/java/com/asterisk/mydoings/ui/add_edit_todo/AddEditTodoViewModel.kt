package com.asterisk.mydoings.ui.add_edit_todo

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asterisk.mydoings.data.Todo
import com.asterisk.mydoings.data.TodoDao
import com.asterisk.mydoings.utils.Constants.ADD_TODO_RESULT_OK
import com.asterisk.mydoings.utils.Constants.EDIT_TODO_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTodoViewModel @ViewModelInject constructor(
    private val todoDao: TodoDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    private val addEditEventChannels = Channel<AddEditEvent>()
    val addEditTodoEvent = addEditEventChannels.receiveAsFlow()

    val todo = state.get<Todo>("todo")

    var whatTodo = state.get<String>("whatTodo") ?: todo?.whatTodo ?: ""
        set(value) {
            field = value
            state.set("whatTodo", value)
        }
    var todoImportance = state.get<Boolean>("todoImportance") ?: todo?.important ?: false
        set(value) {
            field = value
            state.set("todoImportance", value)
        }


    fun onSaveTodoClick() {
        if (whatTodo.isEmpty()) {
            //create an event to display error
            showInValidError("What to-do cannot be empty")
            return
        }

        if (todo != null) {
            val updatedTodo = todo.copy(whatTodo = whatTodo, important = todoImportance)
            updateTodo(updatedTodo)
        } else {
            val newTodo = Todo(whatTodo = whatTodo, important = todoImportance)
            createNewTodo(newTodo)
        }

    }

    private fun showInValidError(whatTodo: String) = viewModelScope.launch {
        addEditEventChannels.send(AddEditEvent.ShowInvalidInputMessage(whatTodo))
    }

    private fun updateTodo(updatedTodo: Todo) = viewModelScope.launch {
        todoDao.updateTodo(updatedTodo)
        //send event success event
        addEditEventChannels.send(AddEditEvent.NavigateBackWithResult(ADD_TODO_RESULT_OK))
    }

    private fun createNewTodo(newTodo: Todo) = viewModelScope.launch {
        todoDao.insertTodo(newTodo)
        //send event success event
        addEditEventChannels.send(AddEditEvent.NavigateBackWithResult(EDIT_TODO_RESULT_OK))
    }


    sealed class AddEditEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditEvent()
    }
}