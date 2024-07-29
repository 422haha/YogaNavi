package com.ssafy.yoganavi.ui.homeUI.lecture.lectureList.lecture

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.databinding.ListItemLectureThumbnailBinding
import com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo.lecture.LectureItemCallback
import com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo.lecture.LectureViewHolder

class LectureAdapter(
    private val navigateToLectureDetailFragment: ((Long) -> Unit)? = null,
    private val sendLikeLecture: (Long) -> Unit
) : PagingDataAdapter<LectureData, LectureViewHolder>(LectureItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemLectureThumbnailBinding.inflate(inflater, parent, false)

        return LectureViewHolder(
            binding = binding,
            navigateToLectureDetailFragment = navigateToLectureDetailFragment,
            sendLikeLecture = sendLikeLecture
        )
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        return getItem(position)?.let { holder.bind(it) } ?: Unit
    }

}
