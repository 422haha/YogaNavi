package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.viewHolder

import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ssafy.yoganavi.databinding.CustomChapterViewBinding
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data.VideoData

class VideoViewHolder(
    private val binding: CustomChapterViewBinding,
    private val addVideoListener: (Int) -> Unit,
    private val deleteListener: (Int) -> Unit,
    private val changeVideoTitle: (String, Int) -> Unit,
    private val changeVideoContent: (String, Int) -> Unit
) : ViewHolder(binding.root) {

    fun bind(data: VideoData) = with(binding) {
        initListener()

        etTitle.setText(data.chapterTitle)
        etContent.setText(data.chapterDescription)
        ivVideo.setOnClickListener { addVideoListener(layoutPosition) }
        tvDeleteBtn.setOnClickListener { deleteListener(layoutPosition) }

        val uri = when {
            data.recordPath.isNotBlank() -> data.recordPath
            data.recordVideo.isNotBlank() -> data.recordVideo
            else -> ""
        }

        if (uri.isNotBlank()) getVideo(uri)
        else cleanVideo()
    }

    private fun getVideo(uri: String) = with(binding) {
        val requestOptions = RequestOptions()
            .frame(0)
            .diskCacheStrategy(DiskCacheStrategy.DATA)

        val circularProgressDrawable = CircularProgressDrawable(root.context).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }

        Glide.with(binding.root)
            .load(uri)
            .apply(requestOptions)
            .placeholder(circularProgressDrawable)
            .into(ivVideo)

        tvAddVideo.visibility = View.GONE
    }

    private fun cleanVideo() = with(binding) {
        Glide.with(binding.root)
            .clear(ivVideo)

        tvAddVideo.visibility = View.VISIBLE
    }

    private fun initListener() = with(binding) {
        etTitle.addTextChangedListener { newText ->
            changeVideoTitle(newText?.toString() ?: "", absoluteAdapterPosition)
        }

        etContent.addTextChangedListener { newText ->
            changeVideoContent(newText?.toString() ?: "", absoluteAdapterPosition)
        }
    }
}