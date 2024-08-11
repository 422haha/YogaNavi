package com.ssafy.yoganavi.data.source.dto.home

import com.google.gson.annotations.SerializedName

data class HomeData(
    val liveId: Int = 0,
    @SerializedName("nickname")
    val teacherName: String = "",
    @SerializedName("profileImageUrlSmall")
    val teacherSmallProfile: String?,
    @SerializedName("profileImageUrl")
    val teacherProfile: String?,
    @SerializedName("teacher")
    val isMyClass: Boolean = false,
    val isOnAir: Boolean = false,
    var liveTitle: String = "",
    var liveContent : String = "",
    var lectureDay: String = "",
    var lectureDate: Long = 0L,
    var startTime: Long = 0L,
    var endTime: Long = 0L,
    var regDate: Long = 0L,
)