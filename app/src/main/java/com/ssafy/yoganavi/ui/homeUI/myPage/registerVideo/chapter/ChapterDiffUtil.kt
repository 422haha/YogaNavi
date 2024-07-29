package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter

import androidx.recyclerview.widget.DiffUtil
import com.ssafy.yoganavi.data.source.dto.lecture.VideoChapterData

class ChapterDiffUtil : DiffUtil.ItemCallback<VideoChapterData>() {

    override fun areItemsTheSame(oldItem: VideoChapterData, newItem: VideoChapterData): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: VideoChapterData, newItem: VideoChapterData): Boolean =
        oldItem == newItem

}
