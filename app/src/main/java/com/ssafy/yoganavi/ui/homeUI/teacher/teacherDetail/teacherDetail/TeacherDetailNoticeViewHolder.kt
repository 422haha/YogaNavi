package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail.teacherDetail

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.databinding.ListItemNoticeBinding
import com.ssafy.yoganavi.ui.utils.formatDashWeekDate
import com.ssafy.yoganavi.ui.utils.loadImageSequentially

class TeacherDetailNoticeViewHolder(
    private val binding: ListItemNoticeBinding
) : ViewHolder(binding.root) {
    fun bind(item: NoticeData) = with(binding) {
        if (item.profileImageSmallUrl.isNullOrBlank()) {
            binding.ivProfile.setImageResource(R.drawable.profilenull)
        } else {
            Glide.with(binding.root)
                .load(item.profileImageSmallUrl)
                .circleCrop()
                .into(ivProfile)

            if (item.imageUrl?.isNotBlank() == true && item.imageUrlSmall?.isNotBlank() == true) {
                ivNotice.loadImageSequentially(item.imageUrlSmall, item.imageUrl)
            }
        }
        tvTeacherNickname.text = item.userName
        tvDate.text = formatDashWeekDate(item.updatedAt)
        tvContent.text = item.content
        tvDeleteBtn.isVisible = false
    }

}