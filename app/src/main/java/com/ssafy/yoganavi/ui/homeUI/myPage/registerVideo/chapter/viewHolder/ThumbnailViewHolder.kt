package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.viewHolder

import android.view.View
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.databinding.CustomThumbnailViewBinding
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data.ThumbnailData
import com.ssafy.yoganavi.ui.utils.loadOriginalImage

class ThumbnailViewHolder(
    private val binding: CustomThumbnailViewBinding,
    private val addImage: () -> Unit,
    private val changeThumbnailTitle: (String) -> Unit,
    private val changeThumbnailContent: (String) -> Unit,
    private val loadS3ImageSequentially: (ImageView, String, String) -> Unit
) : ViewHolder(binding.root) {

    fun bind(thumbnailData: ThumbnailData) = with(binding) {
        initListener()

        thumbnailData.recordTitle?.let { etTitle.setText(it) }
        thumbnailData.recordContent?.let { etContent.setText(it) }

        val imageKey = thumbnailData.imageKey
        val smallImageKey = thumbnailData.smallImageKey
        val thumbnailPath = thumbnailData.recordThumbnailPath

        if (thumbnailPath.isNotBlank()) {
            tvAddThumbnail.visibility = View.GONE
            ivImage.loadOriginalImage(thumbnailPath)
        } else if (imageKey.isNotBlank() && smallImageKey.isNotBlank()) {
            tvAddThumbnail.visibility = View.GONE
            loadS3ImageSequentially(ivImage, imageKey, smallImageKey)
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