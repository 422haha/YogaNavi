package com.ssafy.yoganavi.data.repository.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(@ApplicationContext val context: Context) {
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private val accessKey = stringPreferencesKey("accessToken")
    private val refreshKey = stringPreferencesKey("refreshToken")
    private val fcmKey = stringPreferencesKey("fcmToken")

    val accessToken: Flow<String> = context.datastore.data.catch { emit(emptyPreferences()) }
        .map { preference -> preference[accessKey] ?: "" }

    val refreshToken: Flow<String> = context.datastore.data.catch { emit(emptyPreferences()) }
        .map { preference -> preference[refreshKey] ?: "" }

    private val firebaseToken: Flow<String> = context.datastore.data.catch { emit(emptyPreferences()) }
        .map { preference -> preference[fcmKey] ?: "" }

    suspend fun setAccessToken(accessToken: String) = context.datastore.edit { preference ->
        preference[accessKey] = accessToken
    }

    suspend fun setRefreshToken(refreshToken: String) = context.datastore.edit { preference ->
        preference[refreshKey] = refreshToken
    }

    suspend fun setFcmToken(firebaseToken: String) = context.datastore.edit { preference ->
        preference[fcmKey] = firebaseToken
    }

    suspend fun getFirebaseToken(): String {
        var token = firebaseToken.firstOrNull() ?: ""

        if(token.isBlank()) {
            token = FirebaseMessaging.getInstance().token.await() ?: ""

            setFcmToken(token)
        }

        return token
    }

    suspend fun clearToken() = context.datastore.edit { preference ->
        preference.clear()
    }
}
