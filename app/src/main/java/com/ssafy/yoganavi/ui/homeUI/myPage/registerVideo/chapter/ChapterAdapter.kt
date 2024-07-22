package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ssafy.yoganavi.data.source.lecture.VideoChapterData
import com.ssafy.yoganavi.databinding.CustomChapterViewBinding

class ChapterAdapter(
    private val addVideoListener: (VideoChapterData) -> Unit,
    private val deleteListener: (VideoChapterData) -> Unit
) : ListAdapter<VideoChapterData, ChapterViewHolder>(ChapterDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CustomChapterViewBinding.inflate(layoutInflater, parent, false)
        return ChapterViewHolder(binding, addVideoListener, deleteListener)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}
