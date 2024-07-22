package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter

import android.net.Uri
import android.view.View
import android.widget.MediaController
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.data.source.lecture.VideoChapterData
import com.ssafy.yoganavi.databinding.CustomChapterViewBinding

class ChapterViewHolder(
    private val binding: CustomChapterViewBinding,
    private val addVideoListener: (VideoChapterData) -> Unit,
    private val deleteListener: (VideoChapterData) -> Unit
) : ViewHolder(binding.root) {

    fun bind(data: VideoChapterData) = with(binding) {
        etTitle.setText(data.chapterTitle)
        etContent.setText(data.chapterDescription)
        tvRegisterBtn.setOnClickListener { addVideoListener(data) }
        tvDeleteBtn.setOnClickListener {
            deleteListener(data)
            clearVideo()
        }

        if (data.recordFile != null) {
            val uri = data.recordFile.toUri()
            val mediaController = MediaController(root.context)
            mediaController.setAnchorView(ivVideo)

            ivVideo.apply {
                setVideoURI(uri)
                setMediaController(mediaController)
                setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.start()
                    mediaPlayer.pause()
                }
            }
        }
    }

    private fun clearVideo() {
        binding.ivVideo.stopPlayback()
        binding.ivVideo.setMediaController(null)
        binding.ivVideo.setOnPreparedListener(null)
        binding.ivVideo.setVideoURI(null)
        binding.ivVideo.visibility = View.GONE
        binding.ivVideo.visibility = View.VISIBLE
    }

}