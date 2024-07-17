package com.ssafy.yoganavi.data.auth

import com.ssafy.yoganavi.data.repository.DataStoreRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = runBlocking { dataStoreRepository.refreshToken.firstOrNull() } ?: ""

        Timber.d("refresh!! ${response.code}, $refreshToken")
        return null
    }

}
