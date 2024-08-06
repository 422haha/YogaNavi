 package com.ssafy.yoganavi.ui.homeUI.schedule.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.databinding.ListItemHomeBinding
import com.ssafy.yoganavi.ui.utils.convertDaysToHangle
import com.ssafy.yoganavi.ui.utils.formatDashDate
import com.ssafy.yoganavi.ui.utils.formatTime
import com.ssafy.yoganavi.ui.utils.startSpaceEnd
import com.ssafy.yoganavi.ui.utils.startTildeEnd

class HomeAdapter(
    private val alertLiveDetailDialog: (id: Int, smallImageUri: String?, imageUri: String?, title: String, content: String, isTeacher: Boolean, isOnAir: Boolean) -> Unit
): ListAdapter<HomeData, HomeAdapter.ViewHolder>(HomeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, alertLiveDetailDialog)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(
        private val binding: ListItemHomeBinding,
        private val alertLiveDetailDialog: (id: Int, smallImageUri: String?, imageUri: String?, title: String, content: String, isTeacher: Boolean, isOnAir: Boolean) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeData) {
            with(binding) {
                if(!item.teacherSmallProfile.isNullOrBlank()) {
                    Glide.with(binding.root)
                        .load(item.teacherSmallProfile)
                        .circleCrop()
                        .into(ivProfile)
                }

                if(item.isOnAir) onAir.setBackgroundResource(R.color.red) else onAir.setBackgroundResource(R.color.gray_20)

                tvTeacherNickname.text = item.teacherName

                binding.tvDate.text = startSpaceEnd(formatDashDate(item.lectureDate), convertDaysToHangle(item.lectureDay))

                tvLectureTitle.text = item.liveTitle

                val timeData = startTildeEnd(formatTime(item.startTime), formatTime(item.endTime))
                tvLectureTime.text = timeData

                clDetail.setOnClickListener {
                    alertLiveDetailDialog(item.liveId, item.teacherSmallProfile,item.teacherProfile, item.liveTitle, item.liveContent, item.isMyClass, item.isOnAir)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup,
                     alertLiveDetailDialog: (id: Int, smallImageUri: String?, imageUri: String?, title: String, content: String, isTeacher: Boolean, isOnAir: Boolean) -> Unit): ViewHolder {
                return ViewHolder(ListItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    alertLiveDetailDialog = alertLiveDetailDialog)
            }
        }
    }
}

class HomeDiffCallback: DiffUtil.ItemCallback<HomeData>() {
    override fun areItemsTheSame(oldItem: HomeData, newItem: HomeData): Boolean {
        return oldItem.liveId == newItem.liveId
    }

    override fun areContentsTheSame(oldItem: HomeData, newItem: HomeData): Boolean {
        return oldItem == newItem
    }
}