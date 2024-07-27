package com.ssafy.yoganavi.data.source.teacher

import java.io.Serializable

data class FilterData(
    var sorting: Int = 0,
    val startTime : Long = 0L,
    val endTime : Long = 0L,
    val day : String="",
    val period : Int = 3,
    val maxLiveNum : Int = 1
) : Serializable