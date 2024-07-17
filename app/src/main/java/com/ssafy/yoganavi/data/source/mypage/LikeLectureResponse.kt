package com.ssafy.yoganavi.data.source.mypage

import com.ssafy.yoganavi.data.source.lecture.LectureData

data class LikeLectureResponse(
    val message : String,
    val data : LectureData
)
