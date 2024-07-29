package com.ssafy.yoganavi.data.source.teacher

import java.io.Serializable

data class FilterData(
    var sorting: Int = 0,
    val startTime : Long = 0L,
    val endTime : Long = 0L,
    var day : String="MON,TUE,WED,THU,FRI,SAT,SUN",
    var period : Int = 3,
    var maxLiveNum : Int = 0
) : Serializable