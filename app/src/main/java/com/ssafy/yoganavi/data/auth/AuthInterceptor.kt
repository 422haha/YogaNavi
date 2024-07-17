package com.ssafy.yoganavi.data.auth

import com.ssafy.yoganavi.data.repository.DataStoreRepository
import com.ssafy.yoganavi.ui.utils.MEMBER
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { dataStoreRepository.token.firstOrNull() } ?: ""
        val url = chain.request().url.pathSegments.firstOrNull()
        val requestBuilder = chain.request().newBuilder()

        if (url != MEMBER) requestBuilder.addHeader("Authorization", "Bearer $token")
        val request = requestBuilder.build()
        val response = chain.proceed(request)

        response.header("Authorization")?.let { newToken ->
            runBlocking { dataStoreRepository.setToken(newToken) }
        }
        response.header("Refresh-Token")?.let { newRefreshToken ->
            runBlocking { dataStoreRepository.setRefreshToken(newRefreshToken) }
        }

        return response
    }
}
