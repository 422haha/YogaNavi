package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail.teacherDetail

import androidx.recyclerview.widget.DiffUtil
import com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail.TeacherDetailItem

class TeacherDetailCallback : DiffUtil.ItemCallback<TeacherDetailItem>(){
    override fun areItemsTheSame(oldItem: TeacherDetailItem, newItem: TeacherDetailItem): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(
        oldItem: TeacherDetailItem,
        newItem: TeacherDetailItem
    ): Boolean {
        return oldItem==newItem
    }

}