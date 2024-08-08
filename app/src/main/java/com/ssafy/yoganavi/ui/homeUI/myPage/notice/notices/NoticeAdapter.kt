package com.ssafy.yoganavi.ui.homeUI.myPage.notice.notices

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.databinding.ListItemNoticeBinding

class NoticeAdapter(
    private val navigateToRegisterNoticeFragment: (Int) -> Unit,
    private val noticeDeleteClick: (NoticeData) -> Unit,
    private val loadS3Image: (ImageView, String) -> Unit,
    private val loadS3ImageSequentially: (ImageView, String, String) -> Unit
) : ListAdapter<NoticeData, NoticeViewHolder>(NoticeItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemNoticeBinding.inflate(inflater, parent, false)
        return NoticeViewHolder(
            binding = binding,
            navigateToRegisterNoticeFragment = navigateToRegisterNoticeFragment,
            loadS3Image = loadS3Image,
            loadS3ImageSequentially = loadS3ImageSequentially
        )
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(currentList[position], noticeDeleteClick)
    }
}