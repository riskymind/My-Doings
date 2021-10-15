package com.asterisk.mydoings.ui.todo

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.asterisk.mydoings.R
import com.asterisk.mydoings.data.OnClickItemListener
import com.asterisk.mydoings.data.Todo
import com.asterisk.mydoings.databinding.TodoItemBinding

class TodoFragmentAdapter(private val listener: OnClickItemListener) : ListAdapter<Todo, TodoFragmentAdapter.TodoViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class TodoViewHolder(private val binding: TodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val todo = getItem(position)
                        listener.onItemClick(todo)
                    }
                }

                cbCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val todo = getItem(position)
                        listener.onCheckBoxClicked(todo, cbCompleted.isChecked)
                    }
                }
            }

        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(todo: Todo) {
            val context = binding.root.context
            binding.apply {
                cbCompleted.isChecked = todo.completed
                tvTodo.text = todo.whatTodo
                tvTodo.paint.isStrikeThruText = todo.completed
                if (todo.completed) {
                    todoContainer.setBackgroundResource(R.drawable.todo_completed_shape)
                }
                ivLabelPriority.isVisible = todo.important
                if (todo.important) {
                    todoContainer.setBackgroundResource(R.drawable.todo_important_shape)
                }
            }
        }
    }




    class DiffCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo) =
            oldItem == newItem

    }
}