package com.ssafy.yoganavi.data.source.home

import com.ssafy.yoganavi.data.source.ScheduleData

data class HomeResponse (
    val data: List<ScheduleData>,
    val message: String
)