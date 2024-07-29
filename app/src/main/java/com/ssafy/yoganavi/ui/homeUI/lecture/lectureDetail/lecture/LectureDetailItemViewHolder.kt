package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.data.source.dto.lecture.VideoChapterData
import com.ssafy.yoganavi.databinding.ListItemLectureBinding

class LectureDetailItemViewHolder(
    private val binding: ListItemLectureBinding,
    private val bindVideoInfo: (uri: String, binding: ListItemLectureBinding) -> Unit,
    private val goChapterVideo: (Int) -> Unit
) : ViewHolder(binding.root) {

    fun bind(data: VideoChapterData) = with(binding) {
        tvVideoTitle.text = data.chapterTitle
        tvVideoContent.text = data.chapterDescription

        val uri = when {
            data.recordPath.isNotBlank() -> data.recordPath
            data.recordVideo.isNotBlank() -> data.recordVideo
            else -> ""
        }
        if (uri.isNotBlank()) bindVideoInfo(uri, binding)

        itemView.setOnClickListener { goChapterVideo(layoutPosition) }
    }
}