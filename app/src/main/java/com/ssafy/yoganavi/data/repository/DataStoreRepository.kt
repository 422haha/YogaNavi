package com.ssafy.yoganavi.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(@ApplicationContext val context: Context) {
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private val accessKey = stringPreferencesKey("accessToken")
    private val refreshKey = stringPreferencesKey("refreshToken")
    private val firebaseKey = stringPreferencesKey("firebaseToken")

    val accessToken: Flow<String> = context.datastore.data.catch { emit(emptyPreferences()) }
        .map { preference -> preference[accessKey] ?: "" }

    val refreshToken: Flow<String> = context.datastore.data.catch { emit(emptyPreferences()) }
        .map { preference -> preference[refreshKey] ?: "" }

    val firebaseToken: Flow<String> = context.datastore.data.catch { emit(emptyPreferences()) }
        .map { preference -> preference[firebaseKey] ?: "" }

    suspend fun setAccessToken(accessToken: String) = context.datastore.edit { preference ->
        preference[accessKey] = accessToken
    }

    suspend fun setRefreshToken(refreshToken: String) = context.datastore.edit { preference ->
        preference[refreshKey] = refreshToken
    }

    suspend fun setFirebaseToken(firebaseToken: String) = context.datastore.edit { preference ->
        preference[firebaseKey] = firebaseToken
    }

    suspend fun clearToken() = context.datastore.edit { preference ->
        preference.clear()
    }
}
