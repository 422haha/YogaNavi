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
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(val context: Context) {
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private val accessKey = stringPreferencesKey("accessToken")
    private val refreshKey = stringPreferencesKey("refreshToken")

    val accessToken: Flow<String> = context.datastore.data.catch { emit(emptyPreferences()) }
        .map { preference -> preference[accessKey] ?: "" }

    val refreshToken: Flow<String> = context.datastore.data.catch { emit(emptyPreferences()) }
        .map { preference -> preference[refreshKey] ?: "" }

    suspend fun setAccessToken(accessToken: String) = context.datastore.edit { preference ->
        preference[accessKey] = accessToken
    }

    suspend fun setRefreshToken(refreshToken: String) = context.datastore.edit { preference ->
        preference[refreshKey] = refreshToken
    }

    suspend fun clearToken() = context.datastore.edit { preference ->
        preference.clear()
    }
}
