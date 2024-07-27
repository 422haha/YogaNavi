package com.ssafy.yoganavi.data.source.info

import com.ssafy.yoganavi.data.source.YogaDetailResponse
import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.live.LiveLectureData
import com.ssafy.yoganavi.data.source.mypage.Profile
import com.ssafy.yoganavi.data.source.notice.NoticeData
import com.ssafy.yoganavi.data.source.notice.RegisterNoticeRequest
import com.ssafy.yoganavi.data.source.teacher.TeacherData
import retrofit2.Response

interface InfoDataSource {

    suspend fun getProfile(): Response<YogaDetailResponse<Profile>>

    suspend fun updateProfile(profile: Profile) : Response<YogaDetailResponse<Profile>>

    //TEACHER
    suspend fun getTeacherList(): Response<YogaResponse<TeacherData>>

    // LECTURE
    suspend fun getLectureList(): Response<YogaResponse<LectureData>>

    suspend fun createLecture(lecture: LectureDetailData): Response<YogaDetailResponse<Boolean>>

    suspend fun getLecture(recordedId: Long): Response<YogaDetailResponse<LectureDetailData>>

    suspend fun updateLecture(lecture: LectureDetailData): Response<YogaDetailResponse<Boolean>>

    suspend fun deleteLectures(recordIdList: List<Long>): Response<YogaDetailResponse<Boolean>>

    suspend fun likeLecture(recordedId: Long): Response<YogaDetailResponse<Boolean>>

    // LIVE
    suspend fun getLiveList(): Response<YogaResponse<LiveLectureData>>

    suspend fun getLive(liveId: Int): Response<YogaDetailResponse<LiveLectureData>>

    suspend fun createLive(liveLectureData: LiveLectureData): Response<YogaDetailResponse<Unit>>

    suspend fun updateLive(
        liveLectureData: LiveLectureData,
        liveId: Int
    ): Response<YogaDetailResponse<Unit>>

    suspend fun deleteLive(liveId: Int): Response<YogaDetailResponse<Unit>>

    // NOTICE
    suspend fun getNoticeList(): Response<YogaResponse<NoticeData>>

    suspend fun getNotice(articleId: Int): Response<YogaDetailResponse<NoticeData>>

    suspend fun insertNotice(registerNoticeRequest: RegisterNoticeRequest): Response<YogaDetailResponse<Unit>>

    suspend fun updateNotice(
        registerNoticeRequest: RegisterNoticeRequest,
        articleId: Int
    ): Response<YogaDetailResponse<Unit>>

    suspend fun deleteNotice(articleId: Int): Response<YogaDetailResponse<Unit>>
}