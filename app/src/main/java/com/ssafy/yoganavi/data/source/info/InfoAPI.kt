package com.ssafy.yoganavi.data.source.info

import com.ssafy.yoganavi.data.source.YogaDetailResponse
import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.notice.NoticeData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface InfoAPI {

    @GET("mypage/recorded-lecture/list")
    suspend fun getLectureList(): Response<YogaResponse<LectureData>>

    @GET("mypage/recorded-lecture/list/{recorded_id}")
    suspend fun getLecture(@Path("recorded_id") id: Int): Response<YogaDetailResponse<LectureDetailData>>

    @GET("mypage/notification/user")
    suspend fun getNoticeList() : Response<YogaResponse<NoticeData>>
}
