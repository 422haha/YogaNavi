package com.ssafy.yoganavi.data.source.live

import com.google.gson.annotations.SerializedName

data class LiveLectureData(
    val liveId:Int = 0,
    @SerializedName("userId")
    val teacherId: Int = 0,
    @SerializedName("nickname")
    val teacherName: String = "",
    @SerializedName("profileImageUrl")
    val teacherProfile: String = "",
    var liveTitle: String = "",
    var liveContent : String = "",
    var availableDay: String = "",
    var startDate: Long = -1L,
    var endDate: Long = -1L,
    var startTime: Long = -1L,
    var endTime: Long = -1L,
    var regDate: Long = 0L,
    var maxLiveNum: Int = 0
)