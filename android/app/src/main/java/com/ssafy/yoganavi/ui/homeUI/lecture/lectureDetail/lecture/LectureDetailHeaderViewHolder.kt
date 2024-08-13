package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.databinding.ListItemLectureHeaderBinding

class LectureDetailHeaderViewHolder(
    private val binding: ListItemLectureHeaderBinding,
    private val loadS3ImageSequentially: (ImageView, String, String) -> Unit
) : ViewHolder(binding.root) {

    fun bind(lectureHeader: LectureHeader) = with(binding) {
        tvTitle.text = lectureHeader.recordTitle
        tvContent.text = lectureHeader.recordContent
        loadS3ImageSequentially(
            ivLecturePicture,
            lectureHeader.imageKey,
            lectureHeader.smallImageKey
        )
    }
}
