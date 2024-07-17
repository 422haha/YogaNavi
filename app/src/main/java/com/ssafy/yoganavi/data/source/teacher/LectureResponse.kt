package com.ssafy.yoganavi.data.source.teacher

import com.google.gson.annotations.SerializedName

data class LectureListResponse(
    val data : List<TeacherData>,
    val message : String
)