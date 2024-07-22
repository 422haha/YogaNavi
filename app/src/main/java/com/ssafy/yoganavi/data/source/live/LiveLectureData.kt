package com.ssafy.yoganavi.data.source.live

// 2-2, 5-5, 5-6
data class LiveLectureData(
    val liveId:Int = 0,
    val teacherId: Int = 0,
    val teacherName: String = "",
    val teacherProfile: String = "",
    val liveTitle: String = "",
    val liveContent : String = "",
    val availableDay: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val regTime: Long = 0L,
    val maxNum: Int = 0
)
