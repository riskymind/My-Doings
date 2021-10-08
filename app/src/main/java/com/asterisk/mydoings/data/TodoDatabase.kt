package com.asterisk.mydoings.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.asterisk.mydoings.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Todo::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun getTodoDao(): TodoDao

    class Callback @Inject constructor(
        private val database: Provider<TodoDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
        ): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().getTodoDao()

            applicationScope.launch {
                dao.insertTodo(Todo(whatTodo = "Become a Legend"))
                dao.insertTodo(Todo(whatTodo = "I am a Legend"))
                dao.insertTodo(Todo(whatTodo = "You are great", completed = true))
                dao.insertTodo(Todo(whatTodo = "done understanding Room db", important = true))
                dao.insertTodo(Todo(whatTodo = "dig deep inside coroutines"))
                dao.insertTodo(Todo(whatTodo = "Retrofit is next"))
            }
        }
    }
}