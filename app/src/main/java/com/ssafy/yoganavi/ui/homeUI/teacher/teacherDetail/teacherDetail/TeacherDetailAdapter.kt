package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail.teacherDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.databinding.ListItemNoticeBinding
import com.ssafy.yoganavi.databinding.ListItemTeacherHeaderBinding
import com.ssafy.yoganavi.databinding.ListItemTeacherLectureRecycleBinding
import com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail.TeacherDetailItem
import com.ssafy.yoganavi.ui.utils.HEADER
import com.ssafy.yoganavi.ui.utils.ITEM_LECTURE
import com.ssafy.yoganavi.ui.utils.ITEM_NOTICE

class TeacherDetailAdapter(
    private val goReserve: (Int, String, String, String) -> (Unit),
    private val navigateToLectureDetailFragment: (Long) -> Unit,
    private val sendLikeLecture: (Long) -> Unit,
    private val loadS3Image: (ImageView, String) -> Unit,
    private val loadS3ImageSequentially: (ImageView, String, String) -> Unit
) : ListAdapter<TeacherDetailItem, ViewHolder>(TeacherDetailCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> makeHeaderViewHolder(inflater, parent)
            ITEM_LECTURE -> makeItemLectureViewHolder(inflater, parent)
            ITEM_NOTICE -> makeItemNoticeViewHolder(inflater, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: TeacherDetailItem = currentList[position]
        when (holder) {
            is TeacherDetailHeaderViewHolder -> holder.bind((item as TeacherDetailItem.Header).teacherHeader)
            is TeacherDetailViewHolder -> holder.bind((item as TeacherDetailItem.LectureItem).lectureData)
            is TeacherDetailNoticeViewHolder -> holder.bind((item as TeacherDetailItem.NoticeItem).noticeData)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is TeacherDetailItem.Header -> HEADER
            is TeacherDetailItem.LectureItem -> ITEM_LECTURE
            is TeacherDetailItem.NoticeItem -> ITEM_NOTICE
        }
    }

    private fun makeHeaderViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): TeacherDetailHeaderViewHolder {
        val binding = ListItemTeacherHeaderBinding.inflate(inflater, parent, false)
        return TeacherDetailHeaderViewHolder(binding, goReserve, loadS3ImageSequentially)
    }

    private fun makeItemLectureViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): TeacherDetailViewHolder {
        val binding = ListItemTeacherLectureRecycleBinding.inflate(inflater, parent, false)
        return TeacherDetailViewHolder(
            binding = binding,
            navigateToLectureDetailFragment = navigateToLectureDetailFragment,
            sendLikeLecture = sendLikeLecture,
            loadS3Image = loadS3Image
        )
    }

    private fun makeItemNoticeViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): TeacherDetailNoticeViewHolder {
        val binding = ListItemNoticeBinding.inflate(inflater, parent, false)
        return TeacherDetailNoticeViewHolder(binding, loadS3Image, loadS3ImageSequentially)
    }
}