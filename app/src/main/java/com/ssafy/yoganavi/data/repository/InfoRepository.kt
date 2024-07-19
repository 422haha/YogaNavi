package com.ssafy.yoganavi.data.repository

import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.live.LiveLectureData

interface InfoRepository {

    suspend fun getLectureList(): ListResponse<LectureData>

    suspend fun getLecture(recordId: Int): DetailResponse<LectureDetailData>

    suspend fun getLiveList(): ListResponse<LiveLectureData>

    suspend fun getLive(liveId: Int): DetailResponse<LiveLectureData>
}