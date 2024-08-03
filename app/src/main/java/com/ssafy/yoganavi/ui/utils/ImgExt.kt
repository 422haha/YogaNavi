package com.ssafy.yoganavi.ui.utils

import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

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

fun ImageView.loadOriginalImage(url: String) {
    val circularProgressDrawable = CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }

    Glide.with(this)
        .load(url)
        .placeholder(circularProgressDrawable)
        .into(this)
}

fun ImageView.loadVideoFrame(uri: String, time: Long) {
    val requestOptions = RequestOptions()
        .frame(time)

    val circularProgressDrawable = CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }

    Glide.with(this)
        .load(uri)
        .apply(requestOptions)
        .placeholder(circularProgressDrawable)
        .into(this)
}
