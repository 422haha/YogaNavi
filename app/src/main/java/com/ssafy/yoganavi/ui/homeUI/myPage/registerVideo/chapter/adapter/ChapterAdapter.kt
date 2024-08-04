package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.databinding.CustomChapterViewBinding
import com.ssafy.yoganavi.databinding.CustomThumbnailViewBinding
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data.ChapterItem
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.viewHolder.ThumbnailViewHolder
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.viewHolder.VideoViewHolder
import com.ssafy.yoganavi.ui.utils.HEADER
import com.ssafy.yoganavi.ui.utils.ITEM

class ChapterAdapter(
    private val exoPlayer: ExoPlayer,
    private val addImage: () -> Unit,
    private val addVideoListener: (Int) -> Unit,
    private val deleteListener: (Int) -> Unit,
    private val changeThumbnailTitle: (String) -> Unit,
    private val changeThumbnailContent: (String) -> Unit,
    private val changeVideoTitle: (String, Int) -> Unit,
    private val changeVideoContent: (String, Int) -> Unit
) : ListAdapter<ChapterItem, ViewHolder>(ChapterDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> makeThumbnailViewHolder(inflater, parent)
            ITEM -> makeVideoViewHolder(inflater, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: ChapterItem = currentList[position]
        when (holder) {
            is ThumbnailViewHolder -> holder.bind((item as ChapterItem.ImageItem).thumbnailData)
            is VideoViewHolder -> holder.bind((item as ChapterItem.VideoItem).videoData)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is ChapterItem.ImageItem -> HEADER
            is ChapterItem.VideoItem -> ITEM
        }
    }

    private fun makeThumbnailViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ThumbnailViewHolder {
        val binding = CustomThumbnailViewBinding.inflate(inflater, parent, false)
        return ThumbnailViewHolder(
            binding = binding,
            addImage = addImage,
            changeThumbnailTitle = changeThumbnailTitle,
            changeThumbnailContent = changeThumbnailContent
        )
    }

    private fun makeVideoViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): VideoViewHolder {
        val binding = CustomChapterViewBinding.inflate(inflater, parent, false)
        return VideoViewHolder(
            binding = binding,
            exoPlayer= exoPlayer,
            addVideoListener = addVideoListener,
            deleteListener = deleteListener,
            changeVideoTitle = changeVideoTitle,
            changeVideoContent = changeVideoContent
        )
    }
}
