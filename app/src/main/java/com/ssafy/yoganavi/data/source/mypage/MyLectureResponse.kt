package com.ssafy.yoganavi.data.source.mypage

import com.ssafy.yoganavi.data.source.schedule.ScheduleData

data class MyLectureResponse(
    val message : String,
    val data : ScheduleData
)
