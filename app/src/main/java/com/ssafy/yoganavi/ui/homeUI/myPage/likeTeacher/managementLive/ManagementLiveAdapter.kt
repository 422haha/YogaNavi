package com.ssafy.yoganavi.ui.homeUI.myPage.likeTeacher.managementLive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yoganavi.data.source.live.LiveLectureData
import com.ssafy.yoganavi.databinding.ListItemLiveBinding
import com.ssafy.yoganavi.ui.utils.formatDotDate
import com.ssafy.yoganavi.ui.utils.formatTime

class ManagementLiveAdapter(
    private val navigateToLiveFragment: (Int) -> Unit,
) : ListAdapter<LiveLectureData, ManagementLiveAdapter.ViewHolder>(LiveDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, navigateToLiveFragment)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(
        private val binding: ListItemLiveBinding,
        private val navigateToLiveFragment: (Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LiveLectureData) {
            val date = "${formatDotDate(item.startDate)}~${formatDotDate(item.endDate)}"
            binding.tvDate.text = date

            binding.tvLectureTitle.text = item.liveTitle

            val time = "${formatTime(item.startTime)}~${formatTime(item.endTime)}"
            binding.tvLectureTime.text = "${item.availableDay} | $time"

            binding.vEnterBtn.setOnClickListener {
                navigateToLiveFragment(item.liveId)
            }
        }

        companion object {
            fun from(parent: ViewGroup, navigateToLiveFragment: (Int) -> Unit): ViewHolder {
                return ViewHolder(
                    ListItemLiveBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    navigateToLiveFragment = navigateToLiveFragment
                )
            }
        }
    }
}

class LiveDiffCallback : DiffUtil.ItemCallback<LiveLectureData>() {
    override fun areItemsTheSame(oldItem: LiveLectureData, newItem: LiveLectureData): Boolean {
        return oldItem.liveId == newItem.liveId
    }

    override fun areContentsTheSame(oldItem: LiveLectureData, newItem: LiveLectureData): Boolean {
        return oldItem == newItem
    }
}