package com.ssafy.yoganavi.data.source.lecture

import com.google.gson.annotations.Expose
import java.io.File

data class LectureDetailData(
    val id: Int = 0,
    val recordTitle: String = "",
    val recordContent: String = "",
    val recordThumbnail: String = "",
    val recordedLectureChapters: List<VideoChapterData> = mutableListOf(),

    @Expose(serialize = false, deserialize = false)
    val recordThumbnailFile: File? = null
)