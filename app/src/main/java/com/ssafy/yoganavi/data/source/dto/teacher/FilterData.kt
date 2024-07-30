package com.ssafy.yoganavi.data.source.teacher

import java.io.Serializable

data class FilterData(
    var startTime: Long = 0L,
    var endTime: Long = 86340000L,
    var day: String = "MON,TUE,WED,THU,FRI,SAT,SUN",
    var period: Int = 3,
    var maxLiveNum: Int = 1
) : Serializable