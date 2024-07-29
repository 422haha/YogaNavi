package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.databinding.ListItemLectureBinding
import com.ssafy.yoganavi.databinding.ListItemLectureHeaderBinding
import com.ssafy.yoganavi.ui.utils.HEADER
import com.ssafy.yoganavi.ui.utils.ITEM

class LectureDetailAdapter(
    private val bindVideoInfo: (uri: String, binding: ListItemLectureBinding) -> Unit,
    private val goChapterVideo: (Int) -> Unit
) : ListAdapter<LectureDetailItem, ViewHolder>(LectureDetailItemItemCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> makeHeaderViewHolder(inflater, parent)
            ITEM -> makeItemViewHolder(inflater, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: LectureDetailItem = currentList[position]
        when (holder) {
            is LectureDetailHeaderViewHolder -> holder.bind((item as LectureDetailItem.Header).lectureHeader)
            is LectureDetailItemViewHolder -> holder.bind((item as LectureDetailItem.Item).chapterData)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is LectureDetailItem.Header -> HEADER
            is LectureDetailItem.Item -> ITEM
        }
    }

    private fun makeHeaderViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): LectureDetailHeaderViewHolder {
        val binding = ListItemLectureHeaderBinding.inflate(inflater, parent, false)
        return LectureDetailHeaderViewHolder(binding)
    }

    private fun makeItemViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): LectureDetailItemViewHolder {
        val binding = ListItemLectureBinding.inflate(inflater, parent, false)
        return LectureDetailItemViewHolder(binding, bindVideoInfo, goChapterVideo)
    }
}