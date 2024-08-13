package com.ssafy.yoganavi.ui.homeUI.myPage.modify.hashtag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ssafy.yoganavi.databinding.ListItemHashtagBinding

class HashTagAdapter(
    private val deleteHashTag: (Int) -> Unit
) : ListAdapter<String, HashTagViewHolder>(HashTagCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashTagViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemHashtagBinding.inflate(inflater, parent, false)
        return HashTagViewHolder(binding, deleteHashTag)
    }

    override fun onBindViewHolder(holder: HashTagViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}