package com.asterisk.mydoings.data

interface OnClickItemListener {

    fun onItemClick(todo: Todo)


    fun onCheckBoxClicked(todo: Todo, isChecked: Boolean)
}