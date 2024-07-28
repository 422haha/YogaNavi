package com.ssafy.yoganavi.data.source.dto.lecture

data class LectureDetailData(
    val recordedId: Long = 0,
    val recordTitle: String = "",
    val recordContent: String = "",
    val recordThumbnail: String = "",
    val recordedLectureChapters: MutableList<VideoChapterData> = mutableListOf(),

    @Transient
    val recordThumbnailPath: String = "",
    @Transient
    val thumbnailKey: String = ""
)