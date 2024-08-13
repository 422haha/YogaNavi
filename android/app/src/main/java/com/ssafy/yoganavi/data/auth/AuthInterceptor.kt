package com.ssafy.yoganavi.data.auth

import com.ssafy.yoganavi.data.repository.dataStore.DataStoreRepository
import com.ssafy.yoganavi.ui.utils.AuthManager
import com.ssafy.yoganavi.ui.utils.FORBIDDEN
import com.ssafy.yoganavi.ui.utils.MEMBER
import com.ssafy.yoganavi.ui.utils.NEED_REFRESH_TOKEN
import com.ssafy.yoganavi.ui.utils.REFRESH_TOKEN
import com.ssafy.yoganavi.ui.utils.TOKEN
import com.ssafy.yoganavi.ui.utils.TOKEN_REQUIRED
import com.ssafy.yoganavi.ui.utils.UNAUTHORIZED
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val authManager: AuthManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.addToken()

        if (response.code == UNAUTHORIZED && response.header(TOKEN_REQUIRED) != null) {
            val newTokenResponse = chain.getNewToken()
            newTokenResponse.saveToken()
            response = chain.addToken()
        } else {
            response.saveToken()
        }

        val code = response.code
        if (code == UNAUTHORIZED || code == FORBIDDEN) authManager.authError(NEED_REFRESH_TOKEN)
        return response
    }

    private fun Interceptor.Chain.addToken(): Response {
        val token = runBlocking { dataStoreRepository.accessToken.firstOrNull() } ?: ""
        val url = request().url.pathSegments.firstOrNull()
        val requestBuilder = request().newBuilder()

        if (url != MEMBER) requestBuilder.addHeader(TOKEN, "Bearer $token")
        val request = requestBuilder.build()
        return proceed(request)
    }

    private fun Interceptor.Chain.getNewToken(): Response {
        val token = runBlocking { dataStoreRepository.accessToken.firstOrNull() } ?: ""
        val refreshToken = runBlocking { dataStoreRepository.refreshToken.firstOrNull() } ?: ""
        val request = request()
            .newBuilder()
            .addHeader(TOKEN, "Bearer $token")
            .addHeader(REFRESH_TOKEN, refreshToken)
            .build()

        return proceed(request)
    }

    private fun Response.saveToken() {
        header(TOKEN)?.let { newToken ->
            runBlocking { dataStoreRepository.setAccessToken(newToken) }
        }

        header(REFRESH_TOKEN)?.let { newRefreshToken ->
            runBlocking { dataStoreRepository.setRefreshToken(newRefreshToken) }
        }
    }
}
