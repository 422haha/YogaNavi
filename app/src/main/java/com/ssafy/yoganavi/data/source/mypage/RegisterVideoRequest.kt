package com.ssafy.yoganavi.data.source.mypage

import com.ssafy.yoganavi.data.source.lecture.VideoChapterData

data class RegisterVideoRequest(
    val recordThumbnailUrl : String,
    val recordTitle : String,
    val recordContent : String,
    val videoList : List<VideoChapterData>
)
