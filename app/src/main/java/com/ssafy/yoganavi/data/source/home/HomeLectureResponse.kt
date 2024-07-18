package com.ssafy.yoganavi.data.source.home

import com.ssafy.yoganavi.data.source.live.LectureLiveData

data class HomeLectureResponse(
    val data: LectureLiveData,
    val message: String
)
