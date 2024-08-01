package com.ssafy.yoganavi.data.repository.info

import com.ssafy.yoganavi.data.repository.response.DetailResponse
import com.ssafy.yoganavi.data.repository.response.ListResponse
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.data.source.dto.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.dto.live.LiveLectureData
import com.ssafy.yoganavi.data.source.dto.mypage.Profile
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.data.source.dto.notice.RegisterNoticeRequest
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherDetailData
import com.ssafy.yoganavi.data.source.teacher.FilterData

interface InfoRepository {

    suspend fun getProfile(): DetailResponse<Profile>

    suspend fun updateProfile(profile: Profile): DetailResponse<Profile>

    //TEACHER
    suspend fun getAllTeacherList(sorting: Int, searchKeyword: String): ListResponse<TeacherData>

    suspend fun getTeacherList(
        sorting: Int,
        filter: FilterData,
        searchKeyword: String
    ): ListResponse<TeacherData>

    suspend fun getTeacherDetail(teacherId: Int): DetailResponse<TeacherDetailData>

    suspend fun teacherLikeToggle(teacherId: Int): DetailResponse<Boolean>

    suspend fun getAvailableClass(teacherId: Int, method: Int): ListResponse<LiveLectureData>

    // LECTURE
    suspend fun getLectureList(): ListResponse<LectureData>

    suspend fun createLecture(lecture: LectureDetailData): DetailResponse<Boolean>

    suspend fun getLecture(recordId: Long): DetailResponse<LectureDetailData>

    suspend fun updateLecture(lecture: LectureDetailData): DetailResponse<Boolean>

    suspend fun deleteLectures(recordIdList: List<Long>): DetailResponse<Boolean>

    suspend fun likeLecture(recordedId: Long): DetailResponse<Boolean>

    suspend fun getLikeLectureList(): ListResponse<LectureData>

    // LIVE
    suspend fun getLiveList(): ListResponse<LiveLectureData>

    suspend fun getLive(liveId: Int): DetailResponse<LiveLectureData>

    suspend fun createLive(liveLectureData: LiveLectureData): DetailResponse<Unit>

    suspend fun updateLive(liveLectureData: LiveLectureData, liveId: Int): DetailResponse<Unit>

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

    // Home
    suspend fun getHomeList(): ListResponse<HomeData>

    // CourseHistory
    suspend fun getCourseHistoryList(): ListResponse<HomeData>
}