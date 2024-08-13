package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture

import com.ssafy.yoganavi.data.source.dto.lecture.VideoChapterData

sealed class LectureDetailItem {
    abstract val id: Long

    data class Header(val lectureHeader: LectureHeader) : LectureDetailItem() {
        override val id = lectureHeader.recordedId
    }

    data class Item(val chapterData: VideoChapterData) : LectureDetailItem() {
        override val id = chapterData.id ?: -1
    }

}
