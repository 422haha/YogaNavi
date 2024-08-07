package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail.teacherDetail

import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.databinding.ListItemNoticeBinding
import com.ssafy.yoganavi.ui.utils.formatDashWeekDate

class TeacherDetailNoticeViewHolder(
    private val binding: ListItemNoticeBinding,
    private val loadS3Image: (ImageView, String) -> Unit,
    private val loadS3ImageSequentially: (ImageView, String, String) -> Unit
) : ViewHolder(binding.root) {
    fun bind(item: NoticeData) = with(binding) {

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
        tvDeleteBtn.isVisible = false
    }

}