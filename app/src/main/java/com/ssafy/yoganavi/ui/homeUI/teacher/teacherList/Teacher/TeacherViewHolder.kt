package com.ssafy.yoganavi.ui.homeUI.teacher.teacherList.Teacher

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.data.source.teacher.TeacherData
import com.ssafy.yoganavi.databinding.ListItemTeacherBinding
import com.ssafy.yoganavi.ui.utils.toK

class TeacherViewHolder(
    private val binding: ListItemTeacherBinding,
    private val navigateToTeacherDetailFragment: (Int) -> Unit
) : ViewHolder(binding.root) {
    fun bind(item: TeacherData) = with(binding) {
        tvTeacherNickname.text = item.teacherName
        tvCount.text = item.likes.toK()
        if(item.hashtags.isNotEmpty()){
            tvHashtag.text = item.hashtags.joinToString(" ", "#")
        }
        Glide.with(binding.root)
            .load(item.teacherSmallProfile)
            .into(binding.ivProfile)
        if (item.liked) {
            binding.ivFavoriteColor.isVisible = true
            binding.ivFavorite.isVisible = false
        } else {
            binding.ivFavoriteColor.isVisible = false
            binding.ivFavorite.isVisible = true
        }
        binding.root.setOnClickListener {
            navigateToTeacherDetailFragment(item.teacherId)
        }
    }
}