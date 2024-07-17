package com.ssafy.yoganavi.data.source.live

import com.ssafy.yoganavi.data.source.lecture.LectureData

data class LiveListResponse(
    val message : String,
    val data : List<LiveRecordData>
)