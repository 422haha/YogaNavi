package com.ssafy.yoganavi.ui.homeUI.myPage.notice.notices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ssafy.yoganavi.data.source.notice.NoticeData
import com.ssafy.yoganavi.databinding.ListItemNoticeBinding

class NoticeAdapter(
    private val navigateToRegisterNoticeFragment: (Int) -> Unit
) : ListAdapter<NoticeData, NoticeViewHolder>(NoticeItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemNoticeBinding.inflate(inflater, parent, false)
        return NoticeViewHolder(binding, navigateToRegisterNoticeFragment)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}