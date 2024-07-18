package com.ssafy.yoganavi.data.source.live

data class RegisterLiveRequest(
    val liveTitle: String,
    val liveContent : String,
    val startTime: String,
    val endTime: String,
    val maxLiveNum : Int
)
