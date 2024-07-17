package com.ssafy.yoganavi.data.source.home

import com.google.gson.annotations.SerializedName

data class HomeLectureResponse(
    val data: LectureData,
    val message: String
)
data class LectureData(
    @SerializedName("강의_제목") val lectureTitle: String,
    @SerializedName("강사_이름") val lecturerName: String,
    @SerializedName("강의_내용") val lectureContent: String,
    @SerializedName("강사_이미지") val lecturerImage: String
)
