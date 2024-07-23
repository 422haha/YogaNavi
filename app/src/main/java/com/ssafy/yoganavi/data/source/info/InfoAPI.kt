package com.ssafy.yoganavi.data.source.info

import com.ssafy.yoganavi.data.source.YogaDetailResponse
import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.live.LiveLectureData
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

    // Live
    @GET("mypage/live-lecture-manage")
    suspend fun getLiveList(): Response<YogaResponse<LiveLectureData>>

    @GET("mypage/live-lecture/list/{live_id}")
    suspend fun getLive(@Path("live_id") id: Int): Response<YogaDetailResponse<LiveLectureData>>

    @POST("mypage/live-lecture-manage/create")
    suspend fun createLive(): Response<YogaDetailResponse<Unit>>

    @PUT("mypage/live-lecture-manage/update/{live_id}")
    suspend fun updateLive(liveId: Int): Response<YogaDetailResponse<Unit>>

    @DELETE("mypage/live-lecture-manage/delete/{live_id}")
    suspend fun deleteLive(liveId: Int): Response<YogaDetailResponse<Unit>>

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
