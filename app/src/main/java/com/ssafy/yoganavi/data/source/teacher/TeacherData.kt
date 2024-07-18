package com.ssafy.yoganavi.data.source.teacher

data class TeacherData(
    val teacherName: String,
    val teacherProfile: String,
    val teacherId: Int,
    val hashtags: List<String>,
    val isLiked: Boolean,
    val likes: Int,
)