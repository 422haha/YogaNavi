package com.ssafy.yoganavi.ui.homeUI.teacher.teacherReservation.availableList

import androidx.recyclerview.widget.DiffUtil
import com.ssafy.yoganavi.data.source.dto.live.LiveLectureData

class AvailableViewHolderItemCallback : DiffUtil.ItemCallback<LiveLectureData>() {
    override fun areItemsTheSame(oldItem: LiveLectureData, newItem: LiveLectureData): Boolean {
        return oldItem.liveId == newItem.liveId
    }

    override fun areContentsTheSame(oldItem: LiveLectureData, newItem: LiveLectureData): Boolean {
        return oldItem == newItem
    }
}