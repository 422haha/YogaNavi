package com.ssafy.yoganavi.ui.homeUI.schedule.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.databinding.ListItemHomeBinding
import com.ssafy.yoganavi.ui.utils.convertDaysToHangle
import com.ssafy.yoganavi.ui.utils.formatDashDate
import com.ssafy.yoganavi.ui.utils.formatTime
import com.ssafy.yoganavi.ui.utils.startSpaceEnd
import com.ssafy.yoganavi.ui.utils.startTildeEnd

class HomeAdapter(
    private val alertLiveDetailDialog: (id: Int, smallImageUri: String?, imageUri: String?, title: String, content: String) -> Unit
): ListAdapter<HomeData, HomeAdapter.ViewHolder>(HomeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, alertLiveDetailDialog)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(
        private val binding: ListItemHomeBinding,
        private val alertLiveDetailDialog: (id: Int, smallImageUri: String?, imageUri: String?, title: String, content: String) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeData) {
            with(binding) {
                val circularProgressDrawable = CircularProgressDrawable(binding.root.context).apply {
                    strokeWidth = 5f
                    centerRadius = 30f
                }
                circularProgressDrawable.start()

                if(!item.teacherSmallProfile.isNullOrBlank()) {
                    Glide.with(binding.root)
                        .load(item.teacherSmallProfile)
                        .placeholder(circularProgressDrawable)
                        .into(ivProfile)
                }

                tvTeacherNickname.text = item.teacherName

                binding.tvDate.text = startSpaceEnd(formatDashDate(item.lectureDate), convertDaysToHangle(item.lectureDay))

                tvLectureTitle.text = item.liveTitle

                val timeData = startTildeEnd(formatTime(item.startTime), formatTime(item.endTime))
                tvLectureTime.text = timeData

                clDetail.setOnClickListener {
                    alertLiveDetailDialog(item.liveId, item.teacherSmallProfile,item.teacherProfile, item.liveTitle, item.liveContent)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup,
                     alertLiveDetailDialog: (id: Int, smallImageUri: String?, imageUri: String?, title: String, content: String) -> Unit): ViewHolder {
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