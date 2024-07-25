package com.ssafy.yoganavi.data.repository

import com.ssafy.yoganavi.data.repository.response.DetailResponse
import com.ssafy.yoganavi.data.repository.response.ListResponse
import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.live.LiveLectureData
import com.ssafy.yoganavi.data.source.live.RegisterLiveRequest
import com.ssafy.yoganavi.data.source.mypage.Profile
import com.ssafy.yoganavi.data.source.notice.NoticeData
import com.ssafy.yoganavi.data.source.notice.RegisterNoticeRequest

interface InfoRepository {

    suspend fun getProfile(): DetailResponse<Profile>

    // LECTURE
    suspend fun getLectureList(): ListResponse<LectureData>

    suspend fun createLecture(lecture: LectureDetailData): DetailResponse<Boolean>

    suspend fun getLecture(recordId: Long): DetailResponse<LectureDetailData>

    suspend fun updateLecture(lecture: LectureDetailData): DetailResponse<Boolean>

    suspend fun deleteLectures(recordIdList: List<Long>): DetailResponse<Boolean>

    suspend fun likeLecture(recordedId: Long): DetailResponse<Boolean>

    // LIVE
    suspend fun getLiveList(): ListResponse<LiveLectureData>

    suspend fun getLive(liveId: Int): DetailResponse<LiveLectureData>

    suspend fun createLive(registerLiveRequest : RegisterLiveRequest): DetailResponse<Unit>

    suspend fun updateLive(registerLiveRequest: RegisterLiveRequest, liveId: Int): DetailResponse<Unit>

    suspend fun deleteLive(liveId: Int): DetailResponse<Unit>

    // NOTICE
    suspend fun getNoticeList(): ListResponse<NoticeData>

    suspend fun getNotice(articleId: Int): DetailResponse<NoticeData>

    suspend fun insertNotice(registerNoticeRequest: RegisterNoticeRequest): DetailResponse<Unit>

    suspend fun updateNotice(
        registerNoticeRequest: RegisterNoticeRequest,
        articleId: Int
    ): DetailResponse<Unit>

    suspend fun deleteNotice(articleId: Int): DetailResponse<Unit>
}