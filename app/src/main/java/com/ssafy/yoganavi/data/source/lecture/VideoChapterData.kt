package com.ssafy.yoganavi.data.source.lecture

import java.util.UUID

data class VideoChapterData(
    val chapterTitle: String = "",
    val chapterDescription: String = "",
    val thumbnailUrl: String = "",
    val videoUrl: String = "",
    val id: UUID = UUID.randomUUID()
)
