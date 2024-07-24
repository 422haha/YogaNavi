package com.ssafy.yoganavi.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ssafy.yoganavi.data.source.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreRepository @Inject constructor(val context: Context) {
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private val email = stringPreferencesKey("email")
    private val password = stringPreferencesKey("password")
    private val nickname = stringPreferencesKey("nickname")
    private val teacher = booleanPreferencesKey("teacher")
    private val accessToken = stringPreferencesKey("accessToken")
    private val refreshToken = stringPreferencesKey("refreshToken")

    val userFlow: Flow<User> = context.datastore.data
        .catch {
            emit(emptyPreferences())
        }.map { preferences ->
            val email = preferences[email] ?: ""
            val password = preferences[password] ?: ""
            val nickname = preferences[nickname] ?: ""
            val teacher = preferences[teacher] ?: false
            val accessToken = preferences[accessToken] ?: ""
            val refreshToken = preferences[refreshToken] ?: ""

            User(email, password, nickname, teacher, accessToken, refreshToken)
        }

    suspend fun setUser(user: User) = context.datastore.edit { preference ->
        preference[email] = user.email.ifBlank { preference[email] ?: "" }
        preference[password] = user.password.ifBlank { preference[password] ?: "" }
        preference[nickname] = user.nickname.ifBlank { preference[nickname] ?: "" }
        preference[teacher] = user.teacher
        preference[accessToken] = user.accessToken.ifBlank { preference[accessToken] } ?: ""
        preference[refreshToken] = user.refreshToken.ifBlank { preference[refreshToken] ?: "" }
    }

    suspend fun clearUser() = context.datastore.edit { preference ->
        preference.clear()
    }
}
