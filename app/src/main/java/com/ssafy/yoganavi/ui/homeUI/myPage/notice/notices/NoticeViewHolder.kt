package com.ssafy.yoganavi.ui.homeUI.myPage.notice.notices

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.data.source.notice.NoticeData
import com.ssafy.yoganavi.databinding.ListItemNoticeBinding
import com.ssafy.yoganavi.ui.utils.formatDashWeekDate

class NoticeViewHolder (
    private val binding:ListItemNoticeBinding,
    private val navigateToRegisterNoticeFragment: (Int)->Unit
): ViewHolder(binding.root) {
    fun bind(item: NoticeData,noticeDeleteClick: (NoticeData)->Unit) = with(binding) {
        Glide.with(binding.root)
            .load(item.profileImageUrl)
            .circleCrop()
            .into(ivProfile)
        Glide.with(binding.root)
            .load(item.imageUrl)
            .into(ivNotice)
        tvTeacherNickname.text = item.userName
        tvDate.text =  formatDashWeekDate(item.updatedAt)
        tvContent.text = item.content

        binding.root.setOnClickListener {
            navigateToRegisterNoticeFragment(item.articleId)
        }
        tvDeleteBtn.setOnClickListener {
            noticeDeleteClick(item)
        }
    }
}