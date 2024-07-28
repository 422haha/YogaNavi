package com.ssafy.yoganavi.data.source.dto.teacher

data class TeacherData(
    val teacherName: String,
    val teacherProfile: String,
    val teacherSmallProfile: String,
    val teacherId: Int,
    val hashtags: List<String>,
    val isLiked: Boolean,
    val likes: Int,
)