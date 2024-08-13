package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail

import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData

sealed class TeacherDetailItem {
    abstract val id: Int

    data class Header(val teacherHeader: TeacherData) : TeacherDetailItem() {
        override val id = teacherHeader.teacherId
    }

    data class LectureItem(val lectureData: List<LectureData>) : TeacherDetailItem() {
        override val id = -1//비교 안할 것 같아서 -1 줬음.
    }

    data class NoticeItem(val noticeData: NoticeData) : TeacherDetailItem() {
        override val id = noticeData.articleId
    }
}
