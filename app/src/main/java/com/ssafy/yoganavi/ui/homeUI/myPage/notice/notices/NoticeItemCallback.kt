package com.ssafy.yoganavi.ui.homeUI.myPage.notice.notices

import androidx.recyclerview.widget.DiffUtil
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData

class NoticeItemCallback : DiffUtil.ItemCallback<NoticeData>() {
    override fun areItemsTheSame(oldItem: NoticeData, newItem: NoticeData): Boolean {
        return oldItem.articleId == newItem.articleId
    }

    override fun areContentsTheSame(oldItem: NoticeData, newItem: NoticeData): Boolean {
        return oldItem == newItem
    }
}