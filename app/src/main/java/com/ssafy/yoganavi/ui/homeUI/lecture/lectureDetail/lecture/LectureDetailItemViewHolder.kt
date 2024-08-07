package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.data.source.dto.lecture.VideoChapterData
import com.ssafy.yoganavi.databinding.ListItemLectureBinding
import com.ssafy.yoganavi.ui.utils.loadVideoFrame

class LectureDetailItemViewHolder(
    private val binding: ListItemLectureBinding,
    private val goChapterVideo: (Int) -> Unit,
    private val loadS3VideoFrame: (ImageView, String, Long, Boolean) -> Unit
) : ViewHolder(binding.root) {

    fun bind(data: VideoChapterData) = with(binding) {
        tvVideoTitle.text = data.chapterTitle
        tvVideoContent.text = data.chapterDescription

        var isPath = false
        val uri = when {
            data.recordPath.isNotBlank() -> {
                isPath = true
                data.recordPath
            }

            data.recordKey.isNotBlank() -> {
                isPath = false
                data.recordKey
            }

            else -> ""
        }

        if (uri.isNotBlank()) getVideo(uri, isPath)
        else cleanVideo()

        itemView.setOnClickListener { goChapterVideo(layoutPosition) }
    }

    private fun getVideo(uri: String, isPath: Boolean) = with(binding) {
        if (isPath) ivLecture.loadVideoFrame(uri, 0, false)
        else loadS3VideoFrame(ivLecture, uri, 0, false)
    }

    private fun cleanVideo() = with(binding) {
        ivLecture.setImageDrawable(null)
    }
}