package com.asterisk.mydoings.ui.deleteTodos

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoDeleteDialog: DialogFragment() {

    private val viewModel by viewModels<TodoDeleteDialogViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm deletion")
            .setMessage("Do you really want to delete all todo")
            .setCancelable(true)
            .setNegativeButton("No", null)
            .setPositiveButton("Yes") { _, _, ->
                viewModel.yesDeleteTodo()
            }.create()
}