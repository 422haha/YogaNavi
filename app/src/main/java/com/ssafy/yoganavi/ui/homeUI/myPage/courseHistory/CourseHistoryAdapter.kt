package com.ssafy.yoganavi.ui.homeUI.myPage.courseHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.databinding.ListItemCourseHistoryBinding
import com.ssafy.yoganavi.ui.utils.convertDaysToHangle
import com.ssafy.yoganavi.ui.utils.formatDashDate
import com.ssafy.yoganavi.ui.utils.formatTime
import com.ssafy.yoganavi.ui.utils.startSpaceEnd
import com.ssafy.yoganavi.ui.utils.startTildeEnd

class CourseHistoryAdapter(
    private val loadS3Image: (ImageView, String) -> Unit
) : ListAdapter<HomeData, CourseHistoryAdapter.ViewHolder>(CourseHistoryDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, loadS3Image)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(
        private val binding: ListItemCourseHistoryBinding,
        private val loadS3Image: (ImageView, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeData) {
            with(binding) {

                if (!item.teacherSmallProfile.isNullOrBlank()) {
                    loadS3Image(ivProfile, item.teacherSmallProfile)
                }

                tvTeacherNickname.text = item.teacherName

                binding.tvDate.text = startSpaceEnd(
                    formatDashDate(item.lectureDate),
                    convertDaysToHangle(item.lectureDay)
                )

                tvLectureTitle.text = item.liveTitle

                val timeData = startTildeEnd(formatTime(item.startTime), formatTime(item.endTime))
                tvLectureTime.text = timeData
            }
        }

        companion object {
            fun from(parent: ViewGroup, loadS3Image: (ImageView, String) -> Unit): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListItemCourseHistoryBinding.inflate(inflater, parent, false)
                return ViewHolder(binding, loadS3Image)
            }
        }
    }
}

class CourseHistoryDiffUtilCallback : DiffUtil.ItemCallback<HomeData>() {
    override fun areItemsTheSame(oldItem: HomeData, newItem: HomeData): Boolean {
        return oldItem.liveId == newItem.liveId
    }

    override fun areContentsTheSame(oldItem: HomeData, newItem: HomeData): Boolean {
        return oldItem == newItem
    }
}