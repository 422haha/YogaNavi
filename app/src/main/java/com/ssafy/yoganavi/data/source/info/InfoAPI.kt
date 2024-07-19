package com.ssafy.yoganavi.data.source.info

import com.ssafy.yoganavi.data.source.YogaDetailResponse
import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.live.LiveLectureData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface InfoAPI {

    @GET("mypage/recorded-lecture/list")
    suspend fun getLectureList(): Response<YogaResponse<LectureData>>

    @GET("mypage/recorded-lecture/list/{recorded_id}")
    suspend fun getLecture(@Path("recorded_id") id: Int): Response<YogaDetailResponse<LectureDetailData>>

    @GET("mypage/live-lecture-manage/{user_id}")
    suspend fun getLiveList(): Response<YogaResponse<LiveLectureData>>

    @GET("mypage/live-lecture/list/{live_id}")
    suspend fun getLive(@Path("live_id") id: Int): Response<YogaDetailResponse<LiveLectureData>>
}
