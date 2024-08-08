package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data

data class ThumbnailData(
    val recordedId: Long = -1,
    val recordThumbnailPath: String = "",
    val miniThumbnailPath: String = "",
    val imageKey: String = "",
    val smallImageKey: String = ""
) {
    var recordTitle: String? = null
    var recordContent: String? = null
}