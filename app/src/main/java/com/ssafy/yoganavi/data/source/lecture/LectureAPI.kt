package com.ssafy.yoganavi.data.source.lecture

import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.data.source.response.YogaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LectureAPI {

    @GET("recorded-lecture/sort/{sort}")
    suspend fun getRecordedLectures(
        @Path("sort") sort: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<YogaResponse<LectureData>>

    @GET("recorded-lecture/search/{keyword}/sort/{sort}")
    suspend fun getRecordedLecturesByKeyword(
        @Path("sort") sort: String,
        @Path("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("title") title: Boolean,
        @Query("content") content: Boolean
    ): Response<YogaResponse<LectureData>>

}