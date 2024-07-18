package com.ssafy.yoganavi.data.source

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TestAPI {

    @POST("test/test")
    suspend fun test(@Body str: String): Response<YogaResponse<Unit>>
}