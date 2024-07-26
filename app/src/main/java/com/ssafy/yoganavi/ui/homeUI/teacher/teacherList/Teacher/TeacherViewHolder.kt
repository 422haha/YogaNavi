package com.ssafy.yoganavi.ui.homeUI.teacher.teacherList.Teacher

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.data.source.teacher.TeacherData
import com.ssafy.yoganavi.databinding.ListItemTeacherBinding

class TeacherViewHolder (
    private val binding: ListItemTeacherBinding,
    private val navigateToTeacherDetailFragment: (Int)->Unit
): ViewHolder(binding.root){
    fun bind(item: TeacherData){

    }
}