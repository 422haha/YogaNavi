package com.ssafy.yoganavi.data.source.teacher

import com.google.gson.annotations.SerializedName

data class LectureListResponse(
    val data : List<TeacherData>,
    val message : String
)
data class TeacherData(
    @SerializedName("강사_이름") val teacherName : String,
    @SerializedName("강사_프로필") val teacherProfile : String,
    @SerializedName("강사_id") val teacherId : Int,
    @SerializedName("해시태그") val hashtag : List<String>,
    @SerializedName("좋아요") val isLiked : Boolean,
    @SerializedName("좋아요_수") val likeCount : Int,
)