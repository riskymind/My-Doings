package com.asterisk.mydoings.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = "todo_table")
@Parcelize
data class Todo(
    val whatTodo: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    @PrimaryKey (autoGenerate = true) val id: Int = 0
): Parcelable {
    val createdDateFormatted: String
    get() = DateFormat.getDateTimeInstance().format(createdAt)
}