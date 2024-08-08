package com.ssafy.yoganavi.data.source.dto.lecture

import com.google.gson.annotations.SerializedName

data class LectureData(
    val recordedId: Long,
    val recordTitle: String,
    val nickname: String?,
    val likeCount: Int,
    val myLike: Boolean,

    @SerializedName("recordThumbnail") val imageKey: String,
    @SerializedName("recordThumbnailSmall") val smallImageKey: String,

)
