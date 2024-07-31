package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail.teacherDetail.lecture

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.databinding.ListItemLectureThumbnail2Binding

class TeacherDetailLectureAdapter(
    private val navigateToLectureDetailFragment: (Long)->Unit
) : ListAdapter<LectureData, TeacherDetailLectureViewHolder>(TeacherDetailLectureCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TeacherDetailLectureViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemLectureThumbnail2Binding.inflate(inflater, parent, false)
        return TeacherDetailLectureViewHolder(binding,navigateToLectureDetailFragment)
    }

    override fun onBindViewHolder(holder: TeacherDetailLectureViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}