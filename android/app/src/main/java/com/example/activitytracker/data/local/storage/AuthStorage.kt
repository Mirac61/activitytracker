package com.example.activitytracker.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


// Inspiration from: https://blog.kinto-technologies.com/posts/2025-06-16-encrypted-shared-preferences-migration-en/

class AuthStorage(context: Context) {
    private val context: Context = context.applicationContext
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "auth")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    suspend fun getUserId(): String? {
        return try {
            val prefs = context.dataStore.data.first()
            prefs[USER_ID_KEY]
        }
        catch (e: Exception){
            null
        }
    }

    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { prefs -> prefs[ACCESS_TOKEN_KEY] = token }
    }

    suspend fun getAccessToken(): String? {
        return try {
            val prefs = context.dataStore.data.first()
            prefs[ACCESS_TOKEN_KEY]
        } catch (e: Exception) { null }
    }

    suspend fun saveRefreshToken(token: String) {
        context.dataStore.edit { prefs -> prefs[REFRESH_TOKEN_KEY] = token }
    }

    suspend fun getRefreshToken(): String? {
        return try {
            val prefs = context.dataStore.data.first()
            prefs[REFRESH_TOKEN_KEY]
        } catch (e: Exception) { null }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}