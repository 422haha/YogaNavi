package com.ssafy.yoganavi.data.source.live

import com.google.gson.annotations.SerializedName

data class LiveLectureData(
    val liveId:Int,
    @SerializedName("userId")
    val teacherId: Int,
    @SerializedName("nickname")
    val teacherName: String,
    @SerializedName("profileImageUrl")
    val teacherProfile: String,
    val liveTitle: String,
    val liveContent : String,
    val availableDay: String,
    val startDate: Long,
    val endDate: Long,
    val startTime: Long,
    val endTime: Long,
    val regDate: Long,
    val maxLiveNum: Int
)