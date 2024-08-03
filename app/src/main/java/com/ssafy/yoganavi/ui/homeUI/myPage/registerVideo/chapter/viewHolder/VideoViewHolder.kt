package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.viewHolder

import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.databinding.CustomChapterViewBinding
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data.VideoData
import com.ssafy.yoganavi.ui.utils.loadVideoFrame

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
        ivVideo.loadVideoFrame(uri, 0)
        tvAddVideo.visibility = View.GONE
    }

    private fun cleanVideo() = with(binding) {
        ivVideo.setImageDrawable(null)
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