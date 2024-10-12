package com.ssafy.yoganavi.data.source.home

import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.data.source.response.YogaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeAPI {
    @GET("home")
    suspend fun getHomeList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<YogaResponse<HomeData>>
}