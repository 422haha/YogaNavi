package com.ssafy.yoganavi.data.source

import retrofit2.Response
import javax.inject.Inject

class TestDataSource @Inject constructor(private val testAPI: TestAPI) {
    suspend fun test(str: String): Response<YogaResponse<Unit>> =
        testAPI.test(str)
}