package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.databinding.CustomThumbnailViewBinding

class ThumbnailViewHolder(
    private val binding: CustomThumbnailViewBinding,
    private val addImage: () -> Unit
) : ViewHolder(binding.root) {

    fun bind(thumbnailData: ThumbnailData) = with(binding) {
        etTitle.setText(thumbnailData.recordTitle)
        etContent.setText(thumbnailData.recordContent)

        ivImage.setOnClickListener { addImage() }

        val uri = when {
            thumbnailData.recordThumbnailPath.isNotBlank() -> thumbnailData.recordThumbnailPath
            thumbnailData.recordThumbnail.isNotBlank() -> thumbnailData.recordThumbnail
            else -> ""
        }

        if (uri.isNotBlank()) {
            tvAddThumbnail.visibility = View.GONE

            Glide.with(binding.root)
                .load(uri)
                .into(ivImage)

        } else {
            tvAddThumbnail.visibility = View.VISIBLE
        }
    }
}