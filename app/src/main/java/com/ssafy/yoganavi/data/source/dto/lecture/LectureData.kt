package com.ssafy.yoganavi.data.source.dto.lecture

data class LectureData(
    val recordedId: Long,
    val recordTitle: String,
    val nickname: String?,
    val recordThumbnail: String,
    val recordThumbnailSmall: String,
    val likeCount: Int,
    val myLike: Boolean
)