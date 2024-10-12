package com.ssafy.yoganavi.ui.homeUI.myPage.notice.notices

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.databinding.ListItemNoticeBinding
import com.ssafy.yoganavi.ui.utils.formatDashWeekDate

class NoticeViewHolder(
    private val binding: ListItemNoticeBinding,
    private val navigateToRegisterNoticeFragment: (Int) -> Unit,
    private val loadS3Image: (ImageView, String) -> Unit,
    private val loadS3ImageSequentially: (ImageView, String, String) -> Unit
) : ViewHolder(binding.root) {
    fun bind(item: NoticeData, noticeDeleteClick: (NoticeData) -> Unit) = with(binding) {
        if (item.smallProfileImageKey.isNullOrBlank()) {
            ivProfile.setImageResource(R.drawable.profilenull)
        } else {
            loadS3Image(ivProfile, item.smallProfileImageKey)
        }

        if (!item.imageKey.isNullOrBlank() && !item.smallImageKey.isNullOrBlank()) {
            loadS3ImageSequentially(ivNotice, item.smallImageKey, item.imageKey)
        }

        tvTeacherNickname.text = item.userName
        tvDate.text = formatDashWeekDate(item.updatedAt)
        tvContent.text = item.content

        binding.root.setOnClickListener {
            navigateToRegisterNoticeFragment(item.articleId)
        }
        tvDeleteBtn.setOnClickListener {
            noticeDeleteClick(item)
        }
    }
}