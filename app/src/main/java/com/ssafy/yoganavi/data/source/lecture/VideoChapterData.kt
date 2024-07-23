package com.ssafy.yoganavi.data.source.lecture

import java.util.UUID

data class VideoChapterData(
    val chapterTitle: String = "",
    val chapterDescription: String = "",
    val recordVideo: String = "",

    @Transient
    val id: UUID = UUID.randomUUID(),

    @Transient
    val recordPath: String = "",

    @Transient
    val recordKey: String = ""
)
