package com.ssafy.yoganavi.data.source.live

// 2-2, 5-5, 5-6
data class LiveLectureData(
    val liveId:Int,
    val teacherId: Int,
    val teacherName: String,
    val liveTitle: String,
    val liveContent : String,
    val availableDay: String,
    val startDate: Long,
    val endDate: Long,
    val startTime: Long,
    val endTime: Long,
    val regTime: Long,
    val maxNum: Int
)
