package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter

import androidx.recyclerview.widget.DiffUtil

class ChapterDiffUtil : DiffUtil.ItemCallback<ChapterItem>() {

    override fun areItemsTheSame(oldItem: ChapterItem, newItem: ChapterItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ChapterItem, newItem: ChapterItem): Boolean =
        oldItem == newItem

}
