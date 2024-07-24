package com.ssafy.yoganavi.data.auth

import com.ssafy.yoganavi.data.repository.DataStoreRepository
import com.ssafy.yoganavi.data.source.user.User
import com.ssafy.yoganavi.ui.utils.MEMBER
import com.ssafy.yoganavi.ui.utils.NEED_REFRESH_TOKEN
import com.ssafy.yoganavi.ui.utils.REFRESH_TOKEN
import com.ssafy.yoganavi.ui.utils.TOKEN
import com.ssafy.yoganavi.ui.utils.TOKEN_REQUIRED
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.addToken()
        if (response.code == 401 && response.header(NEED_REFRESH_TOKEN) == TOKEN_REQUIRED) {
            response = chain.getNewToken()
        }

        response.saveToken()
        return response
    }

    private fun Interceptor.Chain.addToken(): Response {
        val user = runBlocking { dataStoreRepository.userFlow.firstOrNull() }
        val token = user?.accessToken ?: ""
        val url = request().url.pathSegments.firstOrNull()
        val requestBuilder = request().newBuilder()

        if (url != MEMBER) requestBuilder.addHeader(TOKEN, "Bearer $token")
        val request = requestBuilder.build()
        return proceed(request)
    }

    private fun Interceptor.Chain.getNewToken(): Response {
        val user = runBlocking { dataStoreRepository.userFlow.firstOrNull() }
        val refreshToken = user?.refreshToken ?: ""
        val request = request()
            .newBuilder()
            .addHeader(REFRESH_TOKEN, refreshToken)
            .build()

        return proceed(request)
    }

    private fun Response.saveToken() {
        header(TOKEN)?.let { newToken ->
            val user = User(accessToken = newToken)
            runBlocking { dataStoreRepository.setUser(user) }
        }

        header(REFRESH_TOKEN)?.let { newRefreshToken ->
            val user = User(refreshToken = newRefreshToken)
            runBlocking { dataStoreRepository.setUser(user) }
        }
    }
}
