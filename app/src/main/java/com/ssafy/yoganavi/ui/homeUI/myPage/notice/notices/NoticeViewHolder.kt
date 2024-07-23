package com.ssafy.yoganavi.ui.homeUI.myPage.notice.notices

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.data.source.notice.NoticeData
import com.ssafy.yoganavi.databinding.ListItemNoticeBinding

class NoticeViewHolder(
    private val binding: ListItemNoticeBinding,
    private val navigateToRegisterNoticeFragment: (Int) -> Unit
) : ViewHolder(binding.root) {
    fun bind(item: NoticeData) = with(binding) {

        Glide.with(binding.root)
            .load(item.imageUrl)
            .circleCrop()
            .into(ivProfile)
        tvTeacherNickname.text = item.userName
        tvDate.text = item.createAt
        tvContent.text = item.content

        tvEditBtn.setOnClickListener {
            navigateToRegisterNoticeFragment(item.articleId)
        }
        tvDeleteBtn.setOnClickListener {
            // TODO: delete구현
        }
    }
}