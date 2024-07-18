package com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo.lecture

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.databinding.ListItemLectureThumbnailBinding
import com.ssafy.yoganavi.ui.utils.toK

class LectureViewHolder(
    private val binding: ListItemLectureThumbnailBinding,
    private val navigateToRegisterVideoFragment: (String) -> Unit
) : ViewHolder(binding.root) {

    private var likeCount = 0

    fun bind(data: LectureData) = with(binding) {
        tvTitle.text = data.recordedTitle
        tvCount.text = data.likes.toK()
        ivFavorite.isSelected = data.likedByUser
        likeCount = data.likes

        val circularProgressDrawable = CircularProgressDrawable(binding.root.context).apply {
            strokeWidth = 5f
            centerRadius = 30f
        }
        circularProgressDrawable.start()

        Glide.with(binding.root)
            .load(data.recordedThumbnail)
            .placeholder(circularProgressDrawable)
            .into(ivThumbnail)

        setListener(data)
    }

    private fun setListener(data: LectureData) = with(binding) {
        ivFavorite.setOnClickListener {
            ivFavorite.isSelected = !ivFavorite.isSelected
            if (ivFavorite.isSelected) likeCount++ else likeCount--
            tvCount.text = likeCount.toK()

            // TODO 서버에 데이터 전송
        }

        ivThumbnail.setOnClickListener {
            navigateToRegisterVideoFragment(data.recordedId)
        }
    }
}