package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail.teacherDetail

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import com.ssafy.yoganavi.databinding.ListItemTeacherHeaderBinding
import com.ssafy.yoganavi.ui.utils.loadImageSequentially

class TeacherDetailHeaderViewHolder(
    private val binding: ListItemTeacherHeaderBinding,
    private val goReserve: (Int, String, String, String) -> (Unit)
) : ViewHolder(binding.root) {
    fun bind(teacherDetailHeader: TeacherData) = with(binding) {
        ivProfile.isVisible = true
        if (teacherDetailHeader.teacherId == -1) {
            ivProfile.isVisible = false
        } else if (teacherDetailHeader.teacherProfile.isNullOrBlank() || teacherDetailHeader.teacherSmallProfile.isNullOrBlank()) {
            ivProfile.setImageResource(R.drawable.profilenull)
        } else if (teacherDetailHeader.teacherProfile.isNotBlank() && teacherDetailHeader.teacherSmallProfile.isNotBlank()) {
            ivProfile.loadImageSequentially(
                teacherDetailHeader.teacherSmallProfile,
                teacherDetailHeader.teacherProfile
            )
        }
        tvNickname.text = teacherDetailHeader.teacherName
        btnReserve.isVisible = tvNickname.text != "공지사항"
        if (teacherDetailHeader.content.isNullOrBlank()) {
            tvContent.isVisible = false
        } else {
            tvContent.text = teacherDetailHeader.content
            tvContent.isVisible = true
        }
        if (teacherDetailHeader.hashtags.isNotEmpty()) {
            tvHashtag.text =
                teacherDetailHeader.hashtags.joinToString(separator = " #", prefix = "#")
            tvHashtag.isVisible = true
        } else {
            tvHashtag.isVisible = false
        }
        btnReserve.setOnClickListener {
            var hashtagString = ""
            if (teacherDetailHeader.hashtags.isNotEmpty())
                hashtagString =
                    teacherDetailHeader.hashtags.joinToString(separator = " #", prefix = "#")
            goReserve(
                teacherDetailHeader.teacherId,
                teacherDetailHeader.teacherName,
                hashtagString,
                teacherDetailHeader.teacherSmallProfile ?: ""
            )
        }
    }
}