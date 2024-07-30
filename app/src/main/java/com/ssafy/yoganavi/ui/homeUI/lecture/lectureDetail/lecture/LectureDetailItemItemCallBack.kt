package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture

import androidx.recyclerview.widget.DiffUtil

class LectureDetailItemItemCallBack : DiffUtil.ItemCallback<LectureDetailItem>() {

    override fun areItemsTheSame(
        oldItem: LectureDetailItem,
        newItem: LectureDetailItem
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: LectureDetailItem,
        newItem: LectureDetailItem
    ): Boolean = oldItem == newItem

}
