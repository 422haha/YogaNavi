package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data

data class ThumbnailData(
    val recordedId: Long = -1,
    val recordThumbnail: String = "",
    val recordThumbnailSmall: String = "",
    val recordThumbnailPath: String = "",
    val miniThumbnailPath: String = "",
    val thumbnailKey: String = "",
    val miniThumbnailKey: String = ""
) {
    var recordTitle: String? = null
    var recordContent: String? = null
}