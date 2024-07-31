package com.ssafy.yoganavi.data.source.info

import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.data.source.dto.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.dto.live.LiveLectureData
import com.ssafy.yoganavi.data.source.dto.mypage.Profile
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.data.source.dto.notice.RegisterNoticeRequest
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherDetailData
import com.ssafy.yoganavi.data.source.response.YogaDetailResponse
import com.ssafy.yoganavi.data.source.response.YogaResponse
import com.ssafy.yoganavi.data.source.teacher.FilterData
import retrofit2.Response

interface InfoDataSource {

    suspend fun getProfile(): Response<YogaDetailResponse<Profile>>

    suspend fun updateProfile(profile: Profile): Response<YogaDetailResponse<Profile>>

    //TEACHER
    suspend fun getAllTeacherList(
        sorting: Int,
        searchKeyword: String
    ): Response<YogaResponse<TeacherData>>

    suspend fun getTeacherList(
        sorting: Int,
        filter: FilterData,
        searchKeyword: String
    ): Response<YogaResponse<TeacherData>>

    suspend fun getTeacherDetail(teacherId: Int): Response<YogaDetailResponse<TeacherDetailData>>

    suspend fun teacherLikeToggle(teacherId: Int): Response<YogaDetailResponse<Boolean>>

    // LECTURE
    suspend fun getLectureList(): Response<YogaResponse<LectureData>>

    suspend fun createLecture(lecture: LectureDetailData): Response<YogaDetailResponse<Boolean>>

    suspend fun getLecture(recordedId: Long): Response<YogaDetailResponse<LectureDetailData>>

    suspend fun updateLecture(lecture: LectureDetailData): Response<YogaDetailResponse<Boolean>>

    suspend fun deleteLectures(recordIdList: List<Long>): Response<YogaDetailResponse<Boolean>>

    suspend fun likeLecture(recordedId: Long): Response<YogaDetailResponse<Boolean>>

    suspend fun getLikeLectureList(): Response<YogaResponse<LectureData>>

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

    // Home
    suspend fun getHomeList(): Response<YogaResponse<HomeData>>

    // CourseHistory
    suspend fun getCourseHistoryList(): Response<YogaResponse<HomeData>>
}