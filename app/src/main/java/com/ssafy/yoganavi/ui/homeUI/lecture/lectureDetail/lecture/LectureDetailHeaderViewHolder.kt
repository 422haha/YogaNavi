package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.databinding.ListItemLectureHeaderBinding
import com.ssafy.yoganavi.ui.utils.loadImageSequentially

class LectureDetailHeaderViewHolder(
    private val binding: ListItemLectureHeaderBinding
) : ViewHolder(binding.root) {

    fun bind(lectureHeader: LectureHeader) = with(binding) {
        tvTitle.text = lectureHeader.recordTitle
        tvContent.text = lectureHeader.recordContent
        ivLecturePicture.loadImageSequentially(
            lectureHeader.recordThumbnailSmall,
            lectureHeader.recordThumbnail
        )
    }

}
