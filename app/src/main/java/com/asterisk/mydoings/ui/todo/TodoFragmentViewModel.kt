package com.asterisk.mydoings.ui.todo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.asterisk.mydoings.data.PreferenceManager
import com.asterisk.mydoings.data.SortOrder
import com.asterisk.mydoings.data.Todo
import com.asterisk.mydoings.data.TodoRepository
import com.asterisk.mydoings.utils.Constants.ADD_TODO_RESULT_OK
import com.asterisk.mydoings.utils.Constants.EDIT_TODO_RESULT_OK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TodoFragmentViewModel @ViewModelInject constructor(
    private val todoRepository: TodoRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    @ExperimentalCoroutinesApi
    val searchQuery = MutableStateFlow("")

    var preferenceFlow = preferenceManager.preferenceFlow


    private val todoEventsChannel = Channel<TodoEvents>()
    val todoEvent = todoEventsChannel.receiveAsFlow()


    @ExperimentalCoroutinesApi
    private val todoFlows = combine(
        searchQuery, preferenceFlow
    ) { searchQuery, filterPreferences ->
        Pair(searchQuery, filterPreferences)
    }.flatMapLatest { (searchQuery, filterPreferences) ->
        todoRepository.getAllTodo(
            searchQuery,
            filterPreferences.sortOrder,
            filterPreferences.hideCompleted
        )
    }

    val allTodo = todoFlows.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClicked(hideComplete: Boolean) = viewModelScope.launch {
        preferenceManager.hideCompleted(hideComplete)
    }

    fun onTodoSelected(todo: Todo) = viewModelScope.launch {
        todoEventsChannel.send(TodoEvents.NavigateToEditTodoScreen(todo))
    }

    fun onTodoItemIsChecked(todo: Todo, checked: Boolean) = viewModelScope.launch {
        todoRepository.updateTodo(todo.copy(completed = checked))
    }

    fun onTodoSwipe(todo: Todo) = viewModelScope.launch {
        todoRepository.deleteTodo(todo)
        todoEventsChannel.send(TodoEvents.ShowUndoDeleteTodoMessage(todo))
    }

    fun undoDeletedTodo(todo: Todo) = viewModelScope.launch {
        todoRepository.insertTodo(todo)
    }

    fun onAddNewTodoClick() = viewModelScope.launch{
        todoEventsChannel.send(TodoEvents.NavigateToAddTodoScreen)
    }

    fun displayActionProgress(result: Int) {
        when(result) {
            ADD_TODO_RESULT_OK -> showTodoSaveConfirmationMsg("What to-do is Saved")
            EDIT_TODO_RESULT_OK -> showTodoEditConfirmationMsg("What to-do is Updated")
        }
    }

    private fun showTodoEditConfirmationMsg(s: String) = viewModelScope.launch{
        todoEventsChannel.send(TodoEvents.ShowTodoSaveConfirmationMsg(s))
    }

    private fun showTodoSaveConfirmationMsg(s: String) = viewModelScope.launch{
        todoEventsChannel.send(TodoEvents.ShowTodoSaveConfirmationMsg(s))
    }


    sealed class TodoEvents() {
        object NavigateToAddTodoScreen: TodoEvents()
        data class NavigateToEditTodoScreen(val todo: Todo): TodoEvents()
        data class ShowUndoDeleteTodoMessage(val todo: Todo): TodoEvents()
        data class ShowTodoSaveConfirmationMsg(val message: String): TodoEvents()
    }
}
