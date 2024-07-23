package com.ssafy.yoganavi.data.source.info

import com.ssafy.yoganavi.data.source.YogaDetailResponse
import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.notice.NoticeData
import com.ssafy.yoganavi.data.source.notice.RegisterNoticeRequest
import retrofit2.Response

interface InfoDataSource {

    suspend fun getLectureList(): Response<YogaResponse<LectureData>>

    suspend fun getLecture(recordedId: Int): Response<YogaDetailResponse<LectureDetailData>>

    suspend fun getNoticeList(): Response<YogaResponse<NoticeData>>

    suspend fun getNotice(articleId : Int): Response<YogaDetailResponse<NoticeData>>

    suspend fun insertNotice(registerNoticeRequest : RegisterNoticeRequest): Response<YogaDetailResponse<Unit>>

    suspend fun updateNotice(registerNoticeRequest : RegisterNoticeRequest, articleId: Int): Response<YogaDetailResponse<Unit>>

    suspend fun deleteNotice(articleId: Int): Response<YogaDetailResponse<Unit>>
}