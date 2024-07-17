package com.ssafy.yoganavi.data.source.mypage

import com.ssafy.yoganavi.data.source.teacher.TeacherData

data class LikeTeacherResponse(
    val message : String,
    val data: List<TeacherData>
)