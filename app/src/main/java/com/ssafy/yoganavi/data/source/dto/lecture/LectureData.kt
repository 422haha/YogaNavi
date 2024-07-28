package com.ssafy.yoganavi.data.source.dto.lecture

data class LectureData(
    val recordedId: Long,
    val recordTitle: String,
    val recordThumbnail: String,
    val recordSmallThumbnail: String,
    val likeCount: Int,
    val myLike: Boolean
)