package com.ssafy.yoganavi.data.source.home

import com.google.gson.annotations.SerializedName

data class HomeResponse (
    val data: List<ScheduleData>,
    val message: String
)

data class ScheduleData(
    @SerializedName("강사_이름") val lecturerName: String,
    @SerializedName("강사_이미지") val lecturerImage: String,
    @SerializedName("강의_제목") val lectureTitle: String,
    @SerializedName("강의_시작_시간") val lectureStartTime: String,
    @SerializedName("강의_종료_시간") val lectureEndTime: String
)