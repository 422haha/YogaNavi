package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter

import com.ssafy.yoganavi.data.source.dto.lecture.VideoChapterData

sealed class ChapterItem {
    abstract val id: Long

    data class ImageItem(val thumbnailData: ThumbnailData) : ChapterItem() {
        override val id: Long = thumbnailData.recordedId
    }

    data class VideoItem(val videoChapterData: VideoChapterData) : ChapterItem() {
        override val id: Long = videoChapterData.id ?: -1
    }

}