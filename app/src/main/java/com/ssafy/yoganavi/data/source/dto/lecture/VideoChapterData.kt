package com.ssafy.yoganavi.data.source.dto.lecture

data class VideoChapterData(
    val id: Long? = null,
    val chapterTitle: String = "",
    val chapterDescription: String = "",
    val recordVideo: String = "",

    @Transient
    val recordPath: String = "",

    @Transient
    val recordKey: String = ""
)
