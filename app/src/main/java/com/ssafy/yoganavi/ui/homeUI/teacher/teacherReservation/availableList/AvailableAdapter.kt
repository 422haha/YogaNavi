package com.ssafy.yoganavi.ui.homeUI.teacher.teacherReservation.availableList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ssafy.yoganavi.data.source.dto.live.LiveLectureData
import com.ssafy.yoganavi.databinding.ListItemAvailableClassBinding

class AvailableAdapter(
    val visibleCalendar : (Long,Long)->(Unit)
) :
    ListAdapter<LiveLectureData, AvailableViewHolder>(AvailableViewHolderItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemAvailableClassBinding.inflate(inflater, parent, false)
        return AvailableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvailableViewHolder, position: Int) {
        holder.bind(currentList[position],visibleCalendar)
    }
}