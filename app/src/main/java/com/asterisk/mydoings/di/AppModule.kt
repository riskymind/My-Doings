package com.asterisk.mydoings.di

import android.app.Application
import androidx.room.Room
import com.asterisk.mydoings.data.TodoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesTodoDatabase(
        app: Application,
        callback: TodoDatabase.Callback
    ) =
        Room.databaseBuilder(app, TodoDatabase::class.java, "todo_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

    @Provides
    fun providesTodoDao(db: TodoDatabase) = db.getTodoDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}


@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope