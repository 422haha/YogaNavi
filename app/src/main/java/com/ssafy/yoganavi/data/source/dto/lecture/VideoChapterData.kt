package com.ssafy.yoganavi.data.source.dto.lecture

import com.google.gson.annotations.SerializedName

data class VideoChapterData(
    val id: Long? = null,
    val chapterTitle: String = "",
    val chapterDescription: String = "",
    val chapterNumber: Int = 0,

    @SerializedName("recordVideo") val recordKey: String = "",

    @Transient val recordPath: String = "",
)
