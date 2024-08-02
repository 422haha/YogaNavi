package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.viewHolder

import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.databinding.CustomThumbnailViewBinding
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data.ThumbnailData

class ThumbnailViewHolder(
    private val binding: CustomThumbnailViewBinding,
    private val addImage: () -> Unit,
    private val changeThumbnailTitle: (String) -> Unit,
    private val changeThumbnailContent: (String) -> Unit
) : ViewHolder(binding.root) {

    fun bind(thumbnailData: ThumbnailData) = with(binding) {
        initListener()

        etTitle.setText(thumbnailData.recordTitle)
        etContent.setText(thumbnailData.recordContent)

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

    private fun initListener() = with(binding) {
        ivImage.setOnClickListener { addImage() }
        etTitle.addTextChangedListener { newText ->
            changeThumbnailTitle(newText?.toString() ?: "")
        }

        etContent.addTextChangedListener { newText ->
            changeThumbnailContent(newText?.toString() ?: "")
        }
    }
}