package com.ssafy.yoganavi.data.source.dto.lecture

import com.google.gson.annotations.SerializedName

data class LectureDetailData(
    val recordedId: Long = 0,
    val recordTitle: String = "",
    val nickname: String = "",
    val recordContent: String = "",
    val recordedLectureChapters: MutableList<VideoChapterData> = mutableListOf(),

    @SerializedName("recordThumbnail") val imageKey: String = "",
    @SerializedName("recordThumbnailSmall") val smallImageKey: String = "",

    @Transient val recordThumbnailPath: String = "",
    @Transient val miniThumbnailPath: String = "",
)
