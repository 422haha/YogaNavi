package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.data.source.lecture.VideoChapterData
import com.ssafy.yoganavi.databinding.CustomChapterViewBinding

class ChapterViewHolder(
    private val binding: CustomChapterViewBinding,
    private val deleteListener: (Int) -> Unit
) : ViewHolder(binding.root) {

    fun bind(data: VideoChapterData) = with(binding) {
        etTitle.setText(data.chapterTitle)
        etContent.setText(data.chapterDescription)
        tvDeleteBtn.setOnClickListener { deleteListener(data.chapterNumber) }
    }
}