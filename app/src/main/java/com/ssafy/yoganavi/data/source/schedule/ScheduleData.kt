package com.ssafy.yoganavi.data.source.schedule

data class ScheduleData(
    val liveId:Int,
    val userId:Int,//강사 id
    val nickname:String,
    val profileImageUrl: String,
    val liveTitle: String,
    val startTime: String,
    val endTime: String
)