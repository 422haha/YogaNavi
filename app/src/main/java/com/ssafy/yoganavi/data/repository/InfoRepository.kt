package com.ssafy.yoganavi.data.repository

import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.notice.NoticeData

interface InfoRepository {

    suspend fun getLectureList(): ListResponse<LectureData>

    suspend fun getLecture(recordId: Int): DetailResponse<LectureDetailData>

    suspend fun getNoticeList() : ListResponse<NoticeData>
}