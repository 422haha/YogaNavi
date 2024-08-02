package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data

sealed class ChapterItem {
    abstract val id: Long

    data class ImageItem(val thumbnailData: ThumbnailData) : ChapterItem() {
        override val id: Long = thumbnailData.recordedId
    }

    data class VideoItem(val videoData: VideoData) : ChapterItem() {
        override val id: Long = videoData.id ?: -1
    }

}