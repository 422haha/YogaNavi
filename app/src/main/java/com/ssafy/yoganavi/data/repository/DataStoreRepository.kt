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
    private val nickname = stringPreferencesKey("nickname")
    private val userImage = stringPreferencesKey("image")
    private val teacher = booleanPreferencesKey("teacher")
    private val accessToken = stringPreferencesKey("accessToken")
    private val refreshToken = stringPreferencesKey("refreshToken")

    val userFlow: Flow<User> = context.datastore.data
        .catch {
            emit(emptyPreferences())
        }.map { preferences ->
            val nickname = preferences[nickname] ?: ""
            val userImage = preferences[userImage] ?: ""
            val teacher = preferences[teacher] ?: false
            val accessToken = preferences[accessToken] ?: ""
            val refreshToken = preferences[refreshToken] ?: ""

            User(nickname, userImage, teacher, accessToken, refreshToken)
        }

    suspend fun setUser(user: User) = context.datastore.edit { preference ->
        preference[nickname] = user.nickname.ifBlank { preference[nickname] ?: "" }
        preference[userImage] = user.imageUrl.ifBlank { preference[userImage] ?: "" }
        preference[teacher] = user.teacher
        preference[accessToken] = user.accessToken.ifBlank { preference[accessToken] } ?: ""
        preference[refreshToken] = user.refreshToken.ifBlank { preference[refreshToken] ?: "" }
    }

    suspend fun clearUser() = context.datastore.edit { preference ->
        preference.clear()
    }
}
