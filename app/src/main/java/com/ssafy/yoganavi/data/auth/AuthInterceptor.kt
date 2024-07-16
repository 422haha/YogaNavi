package com.ssafy.yoganavi.data.auth

import com.ssafy.yoganavi.data.repository.DataStoreRepository
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
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", token)
            .build()

        return chain.proceed(request)
    }
}
