package com.ssafy.yoganavi.data.source.dto.teacher

import com.google.gson.annotations.SerializedName
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData

data class TeacherDetailData(
    @SerializedName("nickname")
    val teacherName: String,
    @SerializedName("profileImageUrl")
    val teacherProfile: String?,
    @SerializedName("profileImageUrlSmall")
    val teacherSmallProfile: String?,
    @SerializedName("id")
    val teacherId: Int,
    @SerializedName("hashtags")
    val hashtags: List<String>,
    val liked: Boolean,
    @SerializedName("likeCount")
    val likes: Int,
    @SerializedName("recordedLectures")
    val teacherRecorded: MutableList<LectureData> = mutableListOf(),
    @SerializedName("notices")
    val teacherNotice: MutableList<NoticeData> = mutableListOf(),
    val content: String?
)
