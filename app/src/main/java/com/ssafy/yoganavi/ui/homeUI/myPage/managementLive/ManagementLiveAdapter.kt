package com.ssafy.yoganavi.ui.homeUI.myPage.managementLive

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yoganavi.data.source.dto.live.LiveLectureData
import com.ssafy.yoganavi.databinding.ListItemLiveBinding
import com.ssafy.yoganavi.ui.utils.CLOSE_LIVE
import com.ssafy.yoganavi.ui.utils.UPDATE
import com.ssafy.yoganavi.ui.utils.convertDaysToHangle
import com.ssafy.yoganavi.ui.utils.formatDotDate
import com.ssafy.yoganavi.ui.utils.formatTime
import com.ssafy.yoganavi.ui.utils.startTildeEnd
import com.ssafy.yoganavi.ui.utils.startVerticalEnd

class ManagementLiveAdapter(
    private val navigateToLiveFragment: (Int, Boolean) -> Unit,
    private val navigateToRegisterFragment: (String, Int) -> Unit,
    private val deleteLive: (Int) -> Unit,
    private val showSnackBar: (String) -> Unit
) : ListAdapter<LiveLectureData, ManagementLiveAdapter.ViewHolder>(LiveDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent,
            navigateToLiveFragment,
            navigateToRegisterFragment,
            deleteLive,
            showSnackBar
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(
        private val binding: ListItemLiveBinding,
        private val navigateToLiveFragment: (Int, Boolean) -> Unit,
        private val navigateToRegisterFragment: (String, Int) -> Unit,
        private val deleteLive: (Int) -> Unit,
        private val showSnackBar: (String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LiveLectureData) {

            val date = "${formatDotDate(item.startDate)} ~ ${formatDotDate(item.endDate)}"

            with(binding) {
                tvDate.text = date

                tvLectureTitle.text = item.liveTitle

                val weekData = convertDaysToHangle(item.availableDay)

                val timeData = startTildeEnd(formatTime(item.startTime), formatTime(item.endTime))

                tvLectureTime.text = startVerticalEnd(weekData, timeData)

                if (item.endDate < System.currentTimeMillis()) {
                    vEnterBtn.isClickable = false
                    vEnterBtn.setBackgroundColor(Color.LTGRAY)
                    vEnterBtn.setOnClickListener { showSnackBar(CLOSE_LIVE) }
                } else {
                    vEnterBtn.setOnClickListener {
                        navigateToLiveFragment(
                            item.liveId,
                            item.isMyClass
                        )
                    }
                }

                root.setOnClickListener { navigateToRegisterFragment(UPDATE, item.liveId) }

                tvDeleteBtn.setOnClickListener { deleteLive(item.liveId) }
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                navigateToLiveFragment: (Int, Boolean) -> Unit,
                navigateToUpdateFragment: (String, Int) -> Unit,
                deleteLive: (Int) -> Unit,
                showSnackBar: (String) -> Unit
            ): ViewHolder {
                return ViewHolder(
                    ListItemLiveBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    navigateToLiveFragment = navigateToLiveFragment,
                    navigateToRegisterFragment = navigateToUpdateFragment,
                    deleteLive = deleteLive,
                    showSnackBar = showSnackBar
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