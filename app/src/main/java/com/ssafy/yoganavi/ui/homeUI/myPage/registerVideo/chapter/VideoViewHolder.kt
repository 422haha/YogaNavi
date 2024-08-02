package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.data.source.dto.lecture.VideoChapterData
import com.ssafy.yoganavi.databinding.CustomChapterViewBinding

class VideoViewHolder(
    private val binding: CustomChapterViewBinding,
    private val addVideoListener: (Int) -> Unit,
    private val deleteListener: (Int) -> Unit,
    private val getVideo: (String, CustomChapterViewBinding) -> Unit
) : ViewHolder(binding.root) {

    fun bind(data: VideoChapterData) = with(binding) {
        etTitle.setText(data.chapterTitle)
        etContent.setText(data.chapterDescription)
        tvRegisterBtn.setOnClickListener { addVideoListener(layoutPosition) }
        tvDeleteBtn.setOnClickListener {
            deleteListener(layoutPosition)
            clearVideo()
        }

        val uri = when {
            data.recordPath.isNotBlank() -> data.recordPath
            data.recordVideo.isNotBlank() -> data.recordVideo
            else -> ""
        }

        if (uri.isNotBlank()) getVideo(uri, binding)
        else clearVideo()
    }

//    private fun addVideo(uri: String) = with(binding) {
//        val mediaItem = MediaItem.fromUri(uri)
//        pvVideo.player = ExoPlayer.Builder(root.context).build().apply {
//            setMediaItem(mediaItem)
//        }
//        pvVideo.player?.prepare()
//
//        tvAddVideo.visibility = View.GONE
//        grayView.visibility = View.GONE
//        pvVideo.visibility = View.VISIBLE
//    }

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