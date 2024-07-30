package com.ssafy.yoganavi.ui.homeUI.teacher.teacherList.teacher

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import com.ssafy.yoganavi.databinding.ListItemTeacherBinding

class TeacherAdapter(
    private val navigateToRegisterTeacherFragment: (Int) -> Unit,
    private val teacherLikeToggle:(Int)->Unit
) : ListAdapter<TeacherData, TeacherViewHolder>(TeacherItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemTeacherBinding.inflate(inflater, parent, false)
        return TeacherViewHolder(binding, navigateToRegisterTeacherFragment)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        holder.bind(currentList[position],teacherLikeToggle)
    }

}