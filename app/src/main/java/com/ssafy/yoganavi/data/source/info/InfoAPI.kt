package com.ssafy.yoganavi.data.source.info

import com.ssafy.yoganavi.data.source.YogaDetailResponse
import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.notice.NoticeData
import com.ssafy.yoganavi.data.source.notice.RegisterNoticeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InfoAPI {

    @GET("mypage/recorded-lecture/list")
    suspend fun getLectureList(): Response<YogaResponse<LectureData>>

    @GET("mypage/recorded-lecture/list/{recorded_id}")
    suspend fun getLecture(@Path("recorded_id") id: Int): Response<YogaDetailResponse<LectureDetailData>>

    @GET("mypage/notification/list")
    suspend fun getNoticeList() : Response<YogaResponse<NoticeData>>

    @GET("mypage/notification/update/{article_id}")
    suspend fun getNotice(@Path("article_id") id:Int) : Response<YogaDetailResponse<NoticeData>>

    @POST("mypage/notification/write")
    suspend fun insertNotice(@Body registerNoticeRequest : RegisterNoticeRequest) : Response<YogaDetailResponse<Unit>>

    @PUT("mypage/notification/update/{article_id}")
    suspend fun updateNotice(@Body registerNoticeRequest : RegisterNoticeRequest, @Path("article_id") id: Int) : Response<YogaDetailResponse<Unit>>

    @DELETE("mypage/notification/delete/{article_id}")
    suspend fun deleteNotice(@Path("article_id") id : Int) : Response<YogaDetailResponse<Unit>>
}
