package com.ssafy.yoganavi.ui.homeUI.schedule.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.data.source.live.LiveLectureData
import com.ssafy.yoganavi.databinding.ListItemHomeBinding
import com.ssafy.yoganavi.ui.utils.StartTildeEnd
import com.ssafy.yoganavi.ui.utils.formatDashDate
import com.ssafy.yoganavi.ui.utils.formatTime

class HomeAdapter(private val alertLiveDetailDialog: (id: Int, imageUri: String, title: String, content: String) -> Unit):
    ListAdapter<LiveLectureData, HomeAdapter.ViewHolder>(HomeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, alertLiveDetailDialog)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(private val binding: ListItemHomeBinding,
                     private val alertLiveDetailDialog: (id: Int, imageUri: String, title: String, content: String) -> Unit):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LiveLectureData) {
            with(binding) {
                val circularProgressDrawable = CircularProgressDrawable(binding.root.context).apply {
                    strokeWidth = 5f
                    centerRadius = 30f
                }
                circularProgressDrawable.start()

                // small size Image
                Glide.with(binding.root)
                    .load(item.teacherProfile)
                    .placeholder(circularProgressDrawable)
                    .into(ivProfile)

                tvTeacherNickname.text = item.teacherName

                // 기간에 존재 하는 요일 별 수업 -> ex.2024-07-11 목
                // 요일을 뒤에 덧붙여야함
                binding.tvDate.text = formatDashDate(item.startDate)

                tvLectureTitle.text = item.liveTitle

                val timeData = StartTildeEnd(formatTime(item.startTime), formatTime(item.endTime))
                tvLectureTime.text = timeData

                clDetail.setOnClickListener { alertLiveDetailDialog(item.liveId, item.teacherProfile, item.liveTitle, item.liveContent) }
            }
        }

        companion object {
            fun from(parent: ViewGroup,
                     alertLiveDetailDialog: (id: Int, imageUri: String, title: String, content: String) -> Unit): ViewHolder {
                return ViewHolder(ListItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    alertLiveDetailDialog = alertLiveDetailDialog)
            }
        }
    }
}

class HomeDiffCallback: DiffUtil.ItemCallback<LiveLectureData>() {
    override fun areItemsTheSame(oldItem: LiveLectureData, newItem: LiveLectureData): Boolean {
        return oldItem.liveId == newItem.liveId
    }

    override fun areContentsTheSame(oldItem: LiveLectureData, newItem: LiveLectureData): Boolean {
        return oldItem == newItem
    }
}