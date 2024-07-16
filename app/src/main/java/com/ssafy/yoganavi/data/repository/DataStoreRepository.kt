package com.ssafy.yoganavi.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreRepository @Inject constructor(val context: Context) {
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "jwt")
    private val tokenKey = stringPreferencesKey("token")
    private val refreshKey = stringPreferencesKey("refresh")

    val token: Flow<String> = context.datastore.data
        .catch { _ ->
            emit(emptyPreferences())
        }
        .map { preferences: Preferences ->
            preferences[tokenKey] ?: ""
        }

    val refreshToken: Flow<String> = context.datastore.data
        .catch { _ ->
            emit(emptyPreferences())
        }
        .map { preferences: Preferences ->
            preferences[refreshKey] ?: ""
        }

    suspend fun setToken(token: String) = context.datastore.edit {
        it[tokenKey] = token
    }

    suspend fun setRefreshToken(refreshToken: String) = context.datastore.edit {
        it[refreshKey] = refreshToken
    }

    suspend fun clearToken() = context.datastore.edit {
        it.clear()
    }
}
