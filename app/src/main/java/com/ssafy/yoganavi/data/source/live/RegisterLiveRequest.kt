package com.ssafy.yoganavi.data.source.live

data class RegisterLiveRequest(
    val teacherId: Int,
    val liveTitle: String,
    val liveContent : String,
    val availableDay: String,
    val startDate: Long,
    val endDate: Long,
    val startTime: Long,
    val endTime: Long,
    val maxNum: Int
)
