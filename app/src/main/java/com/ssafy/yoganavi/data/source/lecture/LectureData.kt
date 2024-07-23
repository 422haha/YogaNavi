package com.ssafy.yoganavi.data.source.lecture

data class LectureData(
    val recordedId: Long,
    val recordTitle: String,
    val recordThumbnail: String,
    val likeCount: Int,
    val myLike: Boolean
)