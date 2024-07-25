package com.ssafy.yoganavi.ui.homeUI.myPage.modify.hashtag

import androidx.recyclerview.widget.DiffUtil

class HashTagCallBack : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem.hashCode() == newItem.hashCode()

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem
}