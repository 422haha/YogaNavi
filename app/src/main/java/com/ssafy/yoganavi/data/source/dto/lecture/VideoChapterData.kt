package com.ssafy.yoganavi.data.source.dto.lecture

data class VideoChapterData(
    val id: Long? = null,
    val chapterTitle: String = "",
    val chapterDescription: String = "",
    val recordVideo: String = "",
    val chapterNumber : Int = 0,

    @Transient
    val recordPath: String = "",

    @Transient
    val recordKey: String = ""
)
