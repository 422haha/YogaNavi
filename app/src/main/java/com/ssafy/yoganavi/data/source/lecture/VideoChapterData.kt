package com.ssafy.yoganavi.data.source.lecture

import com.google.gson.annotations.Expose
import java.io.File
import java.util.UUID

data class VideoChapterData(
    val chapterTitle: String = "",
    val chapterDescription: String = "",
    val recordVideo: String = "",

    @Expose(serialize = false, deserialize = false)
    val id: UUID = UUID.randomUUID(),

    @Expose(serialize = false, deserialize = false)
    val recordFile: File? = null,
)
