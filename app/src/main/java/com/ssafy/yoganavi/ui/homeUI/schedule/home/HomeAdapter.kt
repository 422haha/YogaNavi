package com.ssafy.yoganavi.ui.homeUI.schedule.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.databinding.ListItemHomeBinding
import com.ssafy.yoganavi.ui.utils.convertDaysToHangle
import com.ssafy.yoganavi.ui.utils.formatDashDate
import com.ssafy.yoganavi.ui.utils.formatTime
import com.ssafy.yoganavi.ui.utils.startSpaceEnd
import com.ssafy.yoganavi.ui.utils.startTildeEnd

class HomeAdapter(
    private val alertLiveDetailDialog: (id: Int, smallImageUri: String?, imageUri: String?, title: String, content: String, isTeacher: Boolean) -> Unit,
    private val loadS3Image: (ImageView, String) -> Unit
) : ListAdapter<HomeData, HomeAdapter.ViewHolder>(HomeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, alertLiveDetailDialog, loadS3Image)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position != 0)
            holder.bind(currentList[position], currentList[position - 1].lectureDate)
        else
            holder.bind(currentList[position], currentList[position].lectureDate)
    }

    class ViewHolder(
        private val binding: ListItemHomeBinding,
        private val alertLiveDetailDialog: (id: Int, smallImageUri: String?, imageUri: String?, title: String, content: String, isTeacher: Boolean) -> Unit,
        private val loadS3Image: (ImageView, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeData, preDay: Long) {
            with(binding) {
                if (!item.teacherSmallProfile.isNullOrBlank()) {
                    loadS3Image(ivProfile, item.teacherSmallProfile)
                } else {
                    ivProfile.setImageResource(R.drawable.profilenull)
                }

                if (item.lectureDate != preDay) {
                    dateDivider.isVisible = true
                    tvDivider.isVisible = true
                    tvDivider.text = item.lectureDay
                } else {
                    dateDivider.isVisible = false
                    tvDivider.isVisible = false
                }

                if (item.isOnAir) onAir.setBackgroundResource(R.color.green) else onAir.setBackgroundResource(R.color.gray_20)

                tvTeacherNickname.text = item.teacherName

                binding.tvDate.text = startSpaceEnd(
                    formatDashDate(item.lectureDate),
                    convertDaysToHangle(item.lectureDay)
                )

                tvLectureTitle.text = item.liveTitle
                tvLectureTime.text = item.startTime.makeNextDay(item.endTime)

                clDetail.setOnClickListener {
                    alertLiveDetailDialog(
                        item.liveId,
                        item.teacherSmallProfile,
                        item.teacherProfile,
                        item.liveTitle,
                        item.liveContent,
                        item.isMyClass
                    )
                }
            }
        }

        private fun Long.makeNextDay(endTime: Long): String {
            val start = formatTime(this)
            var end = formatTime(endTime)

            if(this > endTime) end = "익일 $end"
            return startTildeEnd(start, end)
        }

        companion object {
            fun from(
                parent: ViewGroup,
                alertLiveDetailDialog: (id: Int, smallImageUri: String?, imageUri: String?, title: String, content: String, isTeacher: Boolean) -> Unit,
                loadS3Image: (ImageView, String) -> Unit
            ): ViewHolder {
                return ViewHolder(
                    ListItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    alertLiveDetailDialog = alertLiveDetailDialog,
                    loadS3Image = loadS3Image
                )
            }
        }
    }
}

class HomeDiffCallback : DiffUtil.ItemCallback<HomeData>() {
    override fun areItemsTheSame(oldItem: HomeData, newItem: HomeData): Boolean {
        return oldItem.liveId == newItem.liveId
    }

    override fun areContentsTheSame(oldItem: HomeData, newItem: HomeData): Boolean {
        return oldItem == newItem
    }
}