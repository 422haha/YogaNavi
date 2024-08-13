package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail.teacherDetail.lecture

import androidx.recyclerview.widget.DiffUtil
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData

class TeacherDetailLectureCallback : DiffUtil.ItemCallback<LectureData>() {
    override fun areItemsTheSame(oldItem: LectureData, newItem: LectureData): Boolean {
        return oldItem.recordedId == newItem.recordedId
    }

    override fun areContentsTheSame(oldItem: LectureData, newItem: LectureData): Boolean {
        return oldItem == newItem
    }

}