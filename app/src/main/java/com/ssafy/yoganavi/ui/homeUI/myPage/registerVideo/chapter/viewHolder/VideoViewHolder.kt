package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.viewHolder

import android.view.View
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.databinding.CustomChapterViewBinding
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data.VideoData
import com.ssafy.yoganavi.ui.utils.loadVideoFrame

class VideoViewHolder(
    private val binding: CustomChapterViewBinding,
    private val exoPlayer: ExoPlayer,
    private val addVideoListener: (Int) -> Unit,
    private val deleteListener: (Int) -> Unit,
    private val changeVideoTitle: (String, Int) -> Unit,
    private val changeVideoContent: (String, Int) -> Unit,
    private val loadS3VideoFrame: (ImageView, String, Long, Boolean) -> Unit,
    private val makeUrlFromKey: (String) -> String
) : ViewHolder(binding.root) {

    private var uri: String = ""
    private var isPath: Boolean = false

    fun bind(data: VideoData) = with(binding) {
        initListener()

        etTitle.setText(data.chapterTitle)
        etContent.setText(data.chapterDescription)
        ivVideo.setOnClickListener { addVideoListener(layoutPosition) }
        tvDeleteBtn.setOnClickListener { deleteListener(layoutPosition) }

        uri = when {
            data.recordPath.isNotBlank() -> {
                isPath = true
                data.recordPath
            }

            data.recordKey.isNotBlank() -> {
                isPath = false
                data.recordKey
            }

            else -> ""
        }

        if (uri.isNotBlank()) getFirstFrame()
        else cleanVideo()
    }

    private fun getFirstFrame() = with(binding) {
        if (uri.isBlank()) return@with

        removeExoPlayer()
        if (isPath) ivVideo.loadVideoFrame(uri, 0)
        else loadS3VideoFrame(ivVideo, uri, 0, true)

        tvAddVideo.visibility = View.GONE
        pvVideo.visibility = View.GONE
    }

    fun getVideo() = with(binding) {
        if (uri.isBlank()) return@with

        val mediaItem = if (isPath) {
            MediaItem.fromUri(uri)
        } else {
            MediaItem.fromUri(makeUrlFromKey(uri))
        }

        pvVideo.player = exoPlayer.apply {
            setMediaItem(mediaItem)
            volume = 0f
            prepare()

            addListener(object : Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        pvVideo.visibility = View.VISIBLE
                        play()
                        removeListener(this)
                    }
                }
            })
        }
    }

    private fun cleanVideo() = with(binding) {
        removeExoPlayer()
        ivVideo.setImageDrawable(null)
        tvAddVideo.visibility = View.VISIBLE
    }

    fun removeExoPlayer() = with(binding) {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.clearVideoSurface()
        pvVideo.player = null
        pvVideo.visibility = View.GONE
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