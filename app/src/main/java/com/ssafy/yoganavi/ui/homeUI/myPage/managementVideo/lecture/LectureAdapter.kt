package com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo.lecture

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.databinding.ListItemLectureThumbnailBinding

class LectureAdapter(
    private val navigateToRegisterVideoFragment: (Int) -> Unit
) : ListAdapter<LectureData, LectureViewHolder>(LectureItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemLectureThumbnailBinding.inflate(inflater, parent, false)
        return LectureViewHolder(binding, navigateToRegisterVideoFragment)
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}
