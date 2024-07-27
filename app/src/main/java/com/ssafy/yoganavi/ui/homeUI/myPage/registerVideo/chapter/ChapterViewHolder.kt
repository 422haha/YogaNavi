package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter

import android.view.View
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
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

        val uri = when {
            data.recordPath.isNotBlank() -> data.recordPath
            data.recordVideo.isNotBlank() -> data.recordVideo
            else -> ""
        }

        if (uri.isNotBlank()) addVideo(uri)
        else clearVideo()
    }

    private fun addVideo(uri: String) = with(binding) {
        val mediaItem = MediaItem.fromUri(uri)
        pvVideo.player = ExoPlayer.Builder(root.context).build().apply {
            setMediaItem(mediaItem)
        }
        pvVideo.player?.prepare()

        tvAddVideo.visibility = View.GONE
        grayView.visibility = View.GONE
        pvVideo.visibility = View.VISIBLE
    }

    private fun clearVideo() = with(binding) {
        pvVideo.player?.let { player ->
            player.stop()
            player.clearMediaItems()
            player.release()
        }
        pvVideo.player = null
        System.gc()

        tvAddVideo.visibility = View.VISIBLE
        grayView.visibility = View.VISIBLE
        pvVideo.visibility = View.GONE
    }
}