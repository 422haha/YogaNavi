package com.ssafy.yoganavi.data.source

import com.ssafy.yoganavi.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestRepository @Inject constructor(
    private val testDataSource: TestDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun test(str: String): Response<YogaResponse<Unit>> {
        val response = withContext(ioDispatcher) { testDataSource.test(str) }
        return response
    }
}