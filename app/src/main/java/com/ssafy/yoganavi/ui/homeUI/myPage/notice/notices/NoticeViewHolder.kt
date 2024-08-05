package com.ssafy.yoganavi.ui.homeUI.myPage.notice.notices

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.databinding.ListItemNoticeBinding
import com.ssafy.yoganavi.ui.utils.formatDashWeekDate
import com.ssafy.yoganavi.ui.utils.loadImageSequentially

class NoticeViewHolder(
    private val binding: ListItemNoticeBinding,
    private val navigateToRegisterNoticeFragment: (Int) -> Unit
) : ViewHolder(binding.root) {
    fun bind(item: NoticeData, noticeDeleteClick: (NoticeData) -> Unit) = with(binding) {

        Glide.with(binding.root)
            .load(item.profileImageSmallUrl)
            .circleCrop()
            .into(ivProfile)

        if (item.imageUrl?.isNotBlank() == true && item.imageUrlSmall?.isNotBlank() == true) {
            ivNotice.loadImageSequentially(item.imageUrlSmall, item.imageUrl)
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