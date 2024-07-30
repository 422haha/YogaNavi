package com.ssafy.yoganavi.data.repository.info

import com.ssafy.yoganavi.data.repository.response.DetailResponse
import com.ssafy.yoganavi.data.repository.response.ListResponse
import com.ssafy.yoganavi.data.repository.response.toDetailResponse
import com.ssafy.yoganavi.data.repository.response.toListResponse
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.data.source.dto.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.dto.live.LiveLectureData
import com.ssafy.yoganavi.data.source.dto.mypage.Profile
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.data.source.dto.notice.RegisterNoticeRequest
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import com.ssafy.yoganavi.data.source.info.InfoDataSource
import com.ssafy.yoganavi.data.source.teacher.FilterData
import com.ssafy.yoganavi.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoRepositoryImpl @Inject constructor(
    private val infoDataSource: InfoDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : InfoRepository {

    override suspend fun getProfile(): DetailResponse<Profile> {
        val response = withContext(ioDispatcher) { infoDataSource.getProfile() }
        return response.toDetailResponse()
    }

    override suspend fun updateProfile(profile: Profile): DetailResponse<Profile> {
        val response = withContext(ioDispatcher) { infoDataSource.updateProfile(profile) }
        return response.toDetailResponse()
    }

    override suspend fun getAllTeacherList(
        sorting: Int,
        searchKeyword: String
    ): ListResponse<TeacherData> {
        val response =
            withContext(ioDispatcher) { infoDataSource.getAllTeacherList(sorting, searchKeyword) }
        return response.toListResponse()
    }

    override suspend fun getTeacherList(
        sorting: Int,
        filter: FilterData,
        searchKeyword: String
    ): ListResponse<TeacherData> {
        val response = withContext(ioDispatcher) {
            infoDataSource.getTeacherList(
                sorting,
                filter,
                searchKeyword
            )
        }
        return response.toListResponse()
    }

    override suspend fun teacherLikeToggle(teacherId: Int): DetailResponse<Boolean> {
        val response = withContext(ioDispatcher) { infoDataSource.teacherLikeToggle(teacherId) }
        return response.toDetailResponse()
    }

    // LECTURE
    override suspend fun getLectureList(): ListResponse<LectureData> {
        val response = withContext(ioDispatcher) { infoDataSource.getLectureList() }
        return response.toListResponse()
    }

    override suspend fun createLecture(lecture: LectureDetailData): DetailResponse<Boolean> {
        val response = withContext(ioDispatcher) { infoDataSource.createLecture(lecture) }
        return response.toDetailResponse()
    }

    override suspend fun getLecture(recordId: Long): DetailResponse<LectureDetailData> {
        val response = withContext(ioDispatcher) { infoDataSource.getLecture(recordId) }
        return response.toDetailResponse()
    }


    override suspend fun updateLecture(lecture: LectureDetailData): DetailResponse<Boolean> {
        val response = withContext(ioDispatcher) { infoDataSource.updateLecture(lecture) }
        return response.toDetailResponse()
    }

    override suspend fun deleteLectures(recordIdList: List<Long>): DetailResponse<Boolean> {
        val response = withContext(ioDispatcher) { infoDataSource.deleteLectures(recordIdList) }
        return response.toDetailResponse()
    }

    override suspend fun likeLecture(recordedId: Long): DetailResponse<Boolean> {
        val response = withContext(ioDispatcher) { infoDataSource.likeLecture(recordedId) }
        return response.toDetailResponse()
    }

    override suspend fun getLikeLectureList(): ListResponse<LectureData> {
        val response = withContext(ioDispatcher) { infoDataSource.getLikeLectureList() }
        return response.toListResponse()
    }

    // LIVE
    override suspend fun getLiveList(): ListResponse<LiveLectureData> {
        val response = withContext(ioDispatcher) { infoDataSource.getLiveList() }
        return response.toListResponse()
    }

    override suspend fun createLive(liveLectureData: LiveLectureData): DetailResponse<Unit> {
        val response = withContext(ioDispatcher) { infoDataSource.createLive(liveLectureData) }
        return response.toDetailResponse()
    }

    override suspend fun updateLive(
        liveLectureData: LiveLectureData,
        liveId: Int
    ): DetailResponse<Unit> {
        val response =
            withContext(ioDispatcher) { infoDataSource.updateLive(liveLectureData, liveId) }
        return response.toDetailResponse()
    }

    override suspend fun getLive(liveId: Int): DetailResponse<LiveLectureData> {
        val response = withContext(ioDispatcher) { infoDataSource.getLive(liveId) }
        return response.toDetailResponse()
    }

    override suspend fun deleteLive(liveId: Int): DetailResponse<Unit> {
        val response = withContext(ioDispatcher) { infoDataSource.deleteLive(liveId) }
        return response.toDetailResponse()
    }

    // NOTICE
    override suspend fun getNoticeList(): ListResponse<NoticeData> {
        val response = withContext(ioDispatcher) { infoDataSource.getNoticeList() }
        return response.toListResponse()
    }

    override suspend fun insertNotice(registerNoticeRequest: RegisterNoticeRequest): DetailResponse<Unit> {
        val response =
            withContext(ioDispatcher) { infoDataSource.insertNotice(registerNoticeRequest) }
        return response.toDetailResponse()
    }

    override suspend fun getNotice(articleId: Int): DetailResponse<NoticeData> {
        val response = withContext(ioDispatcher) { infoDataSource.getNotice(articleId) }
        return response.toDetailResponse()
    }

    override suspend fun updateNotice(
        registerNoticeRequest: RegisterNoticeRequest,
        articleId: Int
    ): DetailResponse<Unit> {
        val response = withContext(ioDispatcher) {
            infoDataSource.updateNotice(registerNoticeRequest, articleId)
        }
        return response.toDetailResponse()
    }

    override suspend fun deleteNotice(articleId: Int): DetailResponse<Unit> {
        val response = withContext(ioDispatcher) { infoDataSource.deleteNotice(articleId) }
        return response.toDetailResponse()
    }

    override suspend fun getHomeList(): ListResponse<HomeData> {
        val response = withContext(ioDispatcher) { infoDataSource.getHomeList() }
        return response.toListResponse()
    }
}