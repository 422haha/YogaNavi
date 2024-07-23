package com.ssafy.yoganavi.data.repository

import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.live.LiveLectureData
import com.ssafy.yoganavi.data.source.notice.NoticeData
import com.ssafy.yoganavi.data.source.notice.RegisterNoticeRequest
import com.ssafy.yoganavi.data.source.user.UserRequest

interface InfoRepository {

    suspend fun getLectureList(): ListResponse<LectureData>

    suspend fun getLecture(recordId: Int): DetailResponse<LectureDetailData>

    // Live
    suspend fun getLiveList(): ListResponse<LiveLectureData>

    suspend fun getLive(liveId: Int): DetailResponse<LiveLectureData>

    suspend fun createLive(): DetailResponse<Unit>

    suspend fun updateLive(liveId: Int): DetailResponse<Unit>

    suspend fun deleteLive(liveId: Int): DetailResponse<Unit>
    
    suspend fun getNoticeList() : ListResponse<NoticeData>

    suspend fun getNotice(articleId : Int): DetailResponse<NoticeData>

    suspend fun insertNotice(registerNoticeRequest: RegisterNoticeRequest) : DetailResponse<Unit>

    suspend fun updateNotice(registerNoticeRequest: RegisterNoticeRequest, articleId: Int) : DetailResponse<Unit>

    suspend fun deleteNotice(articleId: Int): DetailResponse<Unit>
}