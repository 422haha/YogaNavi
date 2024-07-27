package com.ssafy.yoganavi.ui.utils

import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide

fun ImageView.loadImageSequentially(
    smallUrl: String,
    largeUrl: String
) {
    val circularProgressDrawable = CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }

    Glide.with(this)
        .load(largeUrl)
        .placeholder(circularProgressDrawable)
        .thumbnail(
            Glide.with(this)
                .load(smallUrl)
        ).into(this)
}
