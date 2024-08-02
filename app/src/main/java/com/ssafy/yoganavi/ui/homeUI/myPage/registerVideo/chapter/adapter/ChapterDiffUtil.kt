package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.adapter

import androidx.recyclerview.widget.DiffUtil
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data.ChapterItem

class ChapterDiffUtil : DiffUtil.ItemCallback<ChapterItem>() {

    override fun areItemsTheSame(oldItem: ChapterItem, newItem: ChapterItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ChapterItem, newItem: ChapterItem): Boolean =
        oldItem == newItem

}
