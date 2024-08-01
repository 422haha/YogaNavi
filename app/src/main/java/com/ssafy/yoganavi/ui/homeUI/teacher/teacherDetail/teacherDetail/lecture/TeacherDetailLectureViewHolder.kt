package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail.teacherDetail.lecture


import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.databinding.ListItemLectureThumbnail2Binding
import com.ssafy.yoganavi.ui.utils.toK

class TeacherDetailLectureViewHolder(
    private val binding: ListItemLectureThumbnail2Binding,
    private val navigateToLectureDetailFragment: (Long) -> Unit,
    private val sendLikeLecture: (Long) -> Unit
) : ViewHolder(binding.root) {
    fun bind(data: LectureData) = with(binding) {
        var cnt = data.likeCount
        var isS = data.myLike
        tvTitle.text = data.recordTitle
        tvCount.text = cnt.toK()
        ivFavorite.isSelected = isS


        val circularProgressDrawable = CircularProgressDrawable(binding.root.context).apply {
            strokeWidth = 5f
            centerRadius = 30f
        }
        circularProgressDrawable.start()

        Glide.with(binding.root)
            .load(data.recordThumbnailSmall)
            .placeholder(circularProgressDrawable)
            .into(ivThumbnail)

        binding.root.setOnClickListener {
            navigateToLectureDetailFragment(data.recordedId)
        }
        binding.ivFavorite.setOnClickListener {
            if (isS) cnt -= 1
            else cnt += 1
            tvCount.text = cnt.toK()
            isS = !isS
            ivFavorite.isSelected = isS
            sendLikeLecture(data.recordedId)
        }
    }
}