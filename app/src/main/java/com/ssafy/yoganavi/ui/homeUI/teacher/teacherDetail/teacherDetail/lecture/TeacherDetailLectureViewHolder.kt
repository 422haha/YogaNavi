package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail.teacherDetail.lecture


import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.databinding.ListItemLectureThumbnail2Binding
import com.ssafy.yoganavi.ui.utils.toK

class TeacherDetailLectureViewHolder(
    private val binding: ListItemLectureThumbnail2Binding
) : ViewHolder(binding.root) {
    fun bind(data: LectureData) = with(binding) {
        tvTitle.text = data.recordTitle
        tvCount.text = data.likeCount.toK()
        ivFavorite.isSelected = data.myLike


        val circularProgressDrawable = CircularProgressDrawable(binding.root.context).apply {
            strokeWidth = 5f
            centerRadius = 30f
        }
        circularProgressDrawable.start()

        Glide.with(binding.root)
            .load(data.recordThumbnailSmall)
            .placeholder(circularProgressDrawable)
            .into(ivThumbnail)

    }
}