package com.ssafy.yoganavi.ui.homeUI.teacher.teacherList.teacher

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import com.ssafy.yoganavi.databinding.ListItemTeacherBinding
import com.ssafy.yoganavi.ui.utils.loadImage
import com.ssafy.yoganavi.ui.utils.toK

class TeacherViewHolder(
    private val binding: ListItemTeacherBinding,
    private val navigateToTeacherDetailFragment: (Int) -> Unit
) : ViewHolder(binding.root) {
    fun bind(item: TeacherData, teacherLikeToggle: (Int) -> Unit) = with(binding) {
        tvTeacherNickname.text = item.teacherName
        tvCount.text = item.likes.toK()
        var count = item.likes
        if (item.hashtags.isNotEmpty()) {
            tvHashtag.text = item.hashtags.joinToString(" #", "#")
            tvHashtag.isVisible = true
        } else {
            tvHashtag.isVisible = false
        }

        if (item.teacherSmallProfile.isNullOrBlank()) {
            ivProfile.setImageResource(R.drawable.profilenull)
        } else {
            ivProfile.loadImage(item.teacherSmallProfile)
        }

        if (item.liked) {
            ivFavoriteColor.isVisible = true
            ivFavorite.isVisible = false
        } else {
            ivFavoriteColor.isVisible = false
            ivFavorite.isVisible = true
        }

        root.setOnClickListener {
            navigateToTeacherDetailFragment(item.teacherId)
        }

        ivFavorite.setOnClickListener {
            ivFavoriteColor.isVisible = true
            ivFavorite.isVisible = false
            count += 1
            tvCount.text = count.toString()
            teacherLikeToggle(item.teacherId)
        }

        ivFavoriteColor.setOnClickListener {
            ivFavoriteColor.isVisible = false
            ivFavorite.isVisible = true
            count -= 1
            tvCount.text = count.toString()
            teacherLikeToggle(item.teacherId)
        }
    }
}
