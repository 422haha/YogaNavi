package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.databinding.CustomChapterViewBinding
import com.ssafy.yoganavi.databinding.CustomThumbnailViewBinding
import com.ssafy.yoganavi.ui.utils.HEADER
import com.ssafy.yoganavi.ui.utils.ITEM

class ChapterAdapter(
    private val addImage: () -> Unit,
    private val addVideoListener: (Int) -> Unit,
    private val deleteListener: (Int) -> Unit,
    private val getVideo: (String, CustomChapterViewBinding) -> Unit
) : ListAdapter<ChapterItem, ViewHolder>(ChapterDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> makeThumbnailViewHolder(inflater, parent, addImage)
            ITEM -> makeVideoViewHolder(inflater, parent, addVideoListener, deleteListener, getVideo)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: ChapterItem = currentList[position]
        when (holder) {
            is ThumbnailViewHolder -> holder.bind((item as ChapterItem.ImageItem).thumbnailData)
            is VideoViewHolder -> holder.bind((item as ChapterItem.VideoItem).videoChapterData)
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
        parent: ViewGroup,
        addImage: () -> Unit
    ): ThumbnailViewHolder {
        val binding = CustomThumbnailViewBinding.inflate(inflater, parent, false)
        return ThumbnailViewHolder(binding, addImage)
    }

    private fun makeVideoViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        addVideoListener: (Int) -> Unit,
        deleteListener: (Int) -> Unit,
        getVideo: (String, CustomChapterViewBinding) -> Unit
    ): VideoViewHolder {
        val binding = CustomChapterViewBinding.inflate(inflater, parent, false)
        return VideoViewHolder(binding, addVideoListener, deleteListener, getVideo)
    }
}
