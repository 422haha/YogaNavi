package com.ssafy.yoganavi.data.source.dto.live

import com.google.gson.annotations.SerializedName

data class LiveLectureData(
    val liveId:Int = 0,
    @SerializedName("userId")
    val teacherId: Int = 0,
    @SerializedName("nickname")
    val teacherName: String = "",
    @SerializedName("profileImageUrlSmall")
    val teacherSmallProfile: String?,
    @SerializedName("profileImageUrl")
    val teacherProfile: String?,
    @SerializedName("teacher")
    val isTeacher: Boolean,
    var liveTitle: String = "",
    var liveContent : String = "",
    var availableDay: String = "",
    var startDate: Long = 0L,
    var endDate: Long = 0L,
    var startTime: Long = 0L,
    var endTime: Long = 0L,
    var regDate: Long = 0L,
    var maxLiveNum: Int = 0
)