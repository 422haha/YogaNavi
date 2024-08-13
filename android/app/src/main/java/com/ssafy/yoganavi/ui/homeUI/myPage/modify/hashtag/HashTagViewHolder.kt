package com.ssafy.yoganavi.ui.homeUI.myPage.modify.hashtag

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.databinding.ListItemHashtagBinding

class HashTagViewHolder(
    private val binding: ListItemHashtagBinding,
    private val deleteHashTag: (Int) -> Unit
) : ViewHolder(binding.root) {

    fun bind(hashtag: String) {
        binding.tvHashtag.text = hashtag
        binding.ivDelete.setOnClickListener { deleteHashTag(layoutPosition) }

        val layoutParams = binding.tvHashtag.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        binding.tvHashtag.layoutParams = layoutParams
        itemView.requestLayout()
    }
}