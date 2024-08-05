package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data

data class VideoData(
    val id: Long? = null,
    val recordVideo: String = "",
    val recordPath: String = "",
    val recordKey: String = "",
    val chapterNumber : Int = 0
) {
    var chapterTitle: String? = null
    var chapterDescription: String? = null
}