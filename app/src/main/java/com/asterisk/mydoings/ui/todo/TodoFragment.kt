package com.asterisk.mydoings.ui.todo

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asterisk.mydoings.R
import com.asterisk.mydoings.data.OnClickItemListener
import com.asterisk.mydoings.data.SortOrder
import com.asterisk.mydoings.data.Todo
import com.asterisk.mydoings.databinding.FragmentTodoBinding
import com.asterisk.mydoings.utils.Constants
import com.asterisk.mydoings.utils.Constants.ADD_EDIT_RESULT
import com.asterisk.mydoings.utils.onQueryTextChange
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class TodoFragment : Fragment(R.layout.fragment_todo), OnClickItemListener {

    private val viewModel by viewModels<TodoFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTodoBinding.bind(view)

        setHasOptionsMenu(true)

        val todoAdapter = TodoFragmentAdapter(this)

        binding.apply {
            rvTodo.apply {
                adapter = todoAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val todo = todoAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTodoSwipe(todo)
                }
            }).attachToRecyclerView(rvTodo)

            btnAddTodo.setOnClickListener {
                viewModel.onAddNewTodoClick()
            }
        }

        /**
         * Notify users of action taken
         * Add || Edit Action
         */
        setFragmentResultListener(ADD_EDIT_RESULT) { _, bundle ->
            val result = bundle.getInt(ADD_EDIT_RESULT)
            viewModel.displayActionProgress(result)
        }


        viewModel.allTodo.observe(viewLifecycleOwner) {
            todoAdapter.submitList(it)
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.todoEvent.collect { event ->
                when (event) {
                    is TodoFragmentViewModel.TodoEvents.ShowUndoDeleteTodoMessage -> {
                        Snackbar.make(requireView(), "Todo Deleted!!", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.undoDeletedTodo(event.todo)
                            }.show()
                    }
                    TodoFragmentViewModel.TodoEvents.NavigateToAddTodoScreen -> {
                        val action =
                            TodoFragmentDirections.actionTodoFragmentToAddEditTodoFragment(
                                null, "New todo"
                            )
                        findNavController().navigate(action)
                    }
                    is TodoFragmentViewModel.TodoEvents.NavigateToEditTodoScreen -> {
                        val action =
                            TodoFragmentDirections.actionTodoFragmentToAddEditTodoFragment(
                                event.todo, "Edit Todo"
                            )
                        findNavController().navigate(action)
                    }
                    is TodoFragmentViewModel.TodoEvents.ShowTodoSaveConfirmationMsg -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                    TodoFragmentViewModel.TodoEvents.NavigateToDeleteAllCompleteTodoScreen -> {
                        val action = TodoFragmentDirections.actionGlobalTodoDeleteDialog()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    override fun onItemClick(todo: Todo) {
        viewModel.onTodoSelected(todo)
    }

    override fun onCheckBoxClicked(todo: Todo, isChecked: Boolean) {
        viewModel.onTodoItemIsChecked(todo, isChecked)
    }

    @ExperimentalCoroutinesApi
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.todo_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChange {
            viewModel.searchQuery.value = it
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }

            R.id.action_sort_by_date -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }

            R.id.action_hide_complete_todo -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedClicked(item.isChecked)
                true
            }

            R.id.action_delete_all_complete_todo -> {
                viewModel.onDeleteCompletedTodoClick()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    companion object {
        private const val TAG = "TodoFragment"
    }
}