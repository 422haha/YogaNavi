package com.ssafy.yoganavi.data.source.live

data class RegisterLiveRequest(
    var liveTitle: String = "",
    var liveContent : String = "",
    var availableDay: String = "",
    var startDate: Long = 0L,
    var endDate: Long = 0L,
    var startTime: Long = 0L,
    var endTime: Long = 0L,
    var maxLiveNum: Int = 0
)
