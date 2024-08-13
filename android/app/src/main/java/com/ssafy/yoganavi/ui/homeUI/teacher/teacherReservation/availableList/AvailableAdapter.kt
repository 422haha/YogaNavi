package com.ssafy.yoganavi.ui.homeUI.teacher.teacherReservation.availableList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ssafy.yoganavi.data.source.dto.live.LiveLectureData
import com.ssafy.yoganavi.databinding.ListItemAvailableClassBinding

class AvailableAdapter(
    val visibleCalendar: (Long, Long, Int) -> (Unit)
) :
    ListAdapter<LiveLectureData, AvailableViewHolder>(AvailableViewHolderItemCallback()) {
    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemAvailableClassBinding.inflate(inflater, parent, false)
        return AvailableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvailableViewHolder, position: Int) {
        holder.bind(
            currentList[position],
            position,
            selectedPosition,
            ::onItemSelected,
            visibleCalendar
        )
    }

    private fun onItemSelected(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
    }
}