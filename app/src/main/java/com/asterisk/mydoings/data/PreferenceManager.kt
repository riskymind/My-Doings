package com.asterisk.mydoings.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferenceManager"

enum class SortOrder {
    BY_NAME,
    BY_DATE
}

data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val dataStore = context.createDataStore("user_preference")

    val preferenceFlow = dataStore.data
        .catch { exp ->
            if (exp is IOException) {
                Log.e(TAG, "Error from dataStore preference", exp)
                emit(emptyPreferences())
            }else {
                throw exp
            }
        }
        .map { preference ->
            val sortOrder = SortOrder.valueOf(
                preference[PreferencesKey.SORT_ORDER] ?: SortOrder.BY_DATE.name
            )

            val hideCompleted = preference[PreferencesKey.HIDE_COMPLETED] ?: false

            FilterPreferences(sortOrder, hideCompleted)
        }


    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preference ->
            preference[PreferencesKey.SORT_ORDER] = sortOrder.name
        }
    }


    suspend fun hideCompleted(hideCompleted: Boolean) {
        dataStore.edit { preference ->
            preference[PreferencesKey.HIDE_COMPLETED] = hideCompleted
        }
    }


    private object PreferencesKey {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}