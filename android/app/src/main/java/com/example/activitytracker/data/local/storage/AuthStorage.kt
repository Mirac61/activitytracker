package com.example.activitytracker.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


// Inspiration from: https://blog.kinto-technologies.com/posts/2025-06-16-encrypted-shared-preferences-migration-en/

class AuthStorage(
    private val context: Context,
) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "auth")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    suspend fun getUserId(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[USER_ID_KEY]
    }

    suspend fun clearUserId() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_ID_KEY)
        }
    }
}