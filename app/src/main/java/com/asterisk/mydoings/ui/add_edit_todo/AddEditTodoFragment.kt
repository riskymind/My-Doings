package com.asterisk.mydoings.ui.add_edit_todo

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.asterisk.mydoings.R
import com.asterisk.mydoings.databinding.FragmentAddEditTodoBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditTodoFragment : Fragment(R.layout.fragment_add_edit_todo) {

    private val viewModel by viewModels<AddEditTodoViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTodoBinding.bind(view)

        binding.apply {
            etTodo.setText(viewModel.whatTodo)
            chImportant.isChecked = viewModel.todoImportance
//            chImportant.jumpDrawablesToCurrentState()
            tvDateCreated.isVisible = viewModel.todo != null
            tvDateCreated.text = "Created: ${viewModel.todo?.createdDateFormatted}"


            //take input field
            etTodo.addTextChangedListener {
                viewModel.whatTodo = it.toString()
            }

            chImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.todoImportance = isChecked
            }

            btnSaveTodo.setOnClickListener {
                viewModel.onSaveTodoClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTodoEvent.collect { event ->
                when (event) {
                    is AddEditTodoViewModel.AddEditEvent.NavigateBackWithResult -> {
                        binding.etTodo.clearFocus()
                        setFragmentResult(
                            "add_edit_result",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditTodoViewModel.AddEditEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}