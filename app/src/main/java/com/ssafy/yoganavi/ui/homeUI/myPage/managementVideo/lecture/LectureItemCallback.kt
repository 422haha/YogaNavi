package com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo.lecture

import androidx.recyclerview.widget.DiffUtil
import com.ssafy.yoganavi.data.source.lecture.LectureData

class LectureItemCallback : DiffUtil.ItemCallback<LectureData>() {
    override fun areItemsTheSame(oldItem: LectureData, newItem: LectureData): Boolean =
        oldItem.recordedId == newItem.recordedId

    override fun areContentsTheSame(oldItem: LectureData, newItem: LectureData): Boolean =
        oldItem == newItem

}