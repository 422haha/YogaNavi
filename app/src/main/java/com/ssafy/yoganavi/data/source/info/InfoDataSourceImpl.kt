package com.ssafy.yoganavi.data.source.info

import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.data.source.dto.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.dto.live.LiveLectureData
import com.ssafy.yoganavi.data.source.dto.mypage.Profile
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.data.source.dto.notice.RegisterNoticeRequest
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import com.ssafy.yoganavi.data.source.response.YogaDetailResponse
import com.ssafy.yoganavi.data.source.response.YogaResponse
import com.ssafy.yoganavi.data.source.teacher.FilterData
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoDataSourceImpl @Inject constructor(private val infoAPI: InfoAPI) : InfoDataSource {

    override suspend fun getProfile(): Response<YogaDetailResponse<Profile>> =
        infoAPI.getProfile()

    override suspend fun getAllTeacherList(
        sorting: Int,
        searchKeyword: String
    ): Response<YogaResponse<TeacherData>> =
        infoAPI.getAllTeacherList(sorting, searchKeyword)

    override suspend fun getTeacherList(
        sorting: Int,
        filter: FilterData,
        searchKeyword: String
    ): Response<YogaResponse<TeacherData>> =
        infoAPI.getTeacherList(
            sorting,
            filter.startTime,
            filter.endTime,
            filter.day,
            filter.period,
            filter.maxLiveNum,
            searchKeyword
        )

    override suspend fun teacherLikeToggle(teacherId: Int): Response<YogaDetailResponse<Boolean>> =
        infoAPI.teacherLikeToggle(teacherId)

    override suspend fun updateProfile(profile: Profile): Response<YogaDetailResponse<Profile>> =
        infoAPI.updateProfile(profile)

    // LECTURE
    override suspend fun getLectureList(): Response<YogaResponse<LectureData>> =
        infoAPI.getLectureList()

    override suspend fun createLecture(lecture: LectureDetailData): Response<YogaDetailResponse<Boolean>> =
        infoAPI.createLecture(lecture)

    override suspend fun getLecture(recordedId: Long): Response<YogaDetailResponse<LectureDetailData>> =
        infoAPI.getLecture(recordedId)

    override suspend fun updateLecture(lecture: LectureDetailData): Response<YogaDetailResponse<Boolean>> =
        infoAPI.updateLecture(id = lecture.recordedId, lecture = lecture)

    override suspend fun deleteLectures(recordIdList: List<Long>): Response<YogaDetailResponse<Boolean>> =
        infoAPI.deleteLectures(hashMapOf("lectureIds" to recordIdList))

    override suspend fun likeLecture(recordedId: Long): Response<YogaDetailResponse<Boolean>> =
        infoAPI.likeLecture(recordedId)

    override suspend fun getLikeLectureList(): Response<YogaResponse<LectureData>> =
        infoAPI.getLikeLectureList()

    // LIVE
    override suspend fun getLiveList(): Response<YogaResponse<LiveLectureData>> =
        infoAPI.getLiveList()

    override suspend fun getLive(liveId: Int): Response<YogaDetailResponse<LiveLectureData>> =
        infoAPI.getLive(liveId)

    override suspend fun createLive(liveLectureData: LiveLectureData): Response<YogaDetailResponse<Unit>> =
        infoAPI.createLive(liveLectureData)

    override suspend fun updateLive(
        liveLectureData: LiveLectureData,
        liveId: Int
    ): Response<YogaDetailResponse<Unit>> =
        infoAPI.updateLive(liveLectureData, liveId)

    override suspend fun deleteLive(liveId: Int): Response<YogaDetailResponse<Unit>> =
        infoAPI.deleteLive(liveId)

    // NOTICE
    override suspend fun getNoticeList(): Response<YogaResponse<NoticeData>> =
        infoAPI.getNoticeList()

    override suspend fun getNotice(articleId: Int): Response<YogaDetailResponse<NoticeData>> =
        infoAPI.getNotice(articleId)

    override suspend fun insertNotice(registerNoticeRequest: RegisterNoticeRequest): Response<YogaDetailResponse<Unit>> =
        infoAPI.insertNotice(registerNoticeRequest)

    override suspend fun updateNotice(
        registerNoticeRequest: RegisterNoticeRequest,
        articleId: Int
    ): Response<YogaDetailResponse<Unit>> = infoAPI.updateNotice(registerNoticeRequest, articleId)

    override suspend fun deleteNotice(articleId: Int): Response<YogaDetailResponse<Unit>> =
        infoAPI.deleteNotice(articleId)

    // Home
    override suspend fun getHomeList(): Response<YogaResponse<HomeData>> =
        infoAPI.getHomeList()

    // CourseHistory
    override suspend fun getCourseHistoryList(): Response<YogaResponse<HomeData>> =
        infoAPI.getCourseHistoryList()
}