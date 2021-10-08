package com.asterisk.mydoings.ui.todo

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asterisk.mydoings.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoFragment: Fragment(R.layout.fragment_todo) {

    private val viewModel by viewModels<TodoFragmentViewModel>()

}