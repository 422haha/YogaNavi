package com.ssafy.yoganavi.ui.utils

import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.Date

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

fun ImageView.loadS3ImageSequentially(
    smallKey: String,
    largeKey: String,
    s3Client: AmazonS3Client
) {
    val smallUrl = smallKey.keyToUrl(s3Client)
    val largeUrl = largeKey.keyToUrl(s3Client)
    loadImageSequentially(smallUrl, largeUrl)
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

fun ImageView.loadVideoFrame(uri: String, time: Long, isCircularOn: Boolean = true) {
    val requestOptions = RequestOptions()
        .frame(time)

    val circularProgressDrawable = if (isCircularOn) CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    } else null

    Glide.with(this)
        .load(uri)
        .apply(requestOptions)
        .placeholder(circularProgressDrawable)
        .into(this)
}

fun ImageView.loadImage(uri: String) = Glide.with(this)
    .load(uri)
    .centerCrop()
    .into(this)

fun String.keyToUrl(s3Client: AmazonS3Client): String {
    val date = Date()
    val oneDay = date.time + 1000 * 3600
    date.time = oneDay

    val generatedUrlRequest = GeneratePresignedUrlRequest(BUCKET_NAME, this@keyToUrl)
        .withMethod(HttpMethod.GET)
        .withExpiration(date)

    return s3Client.generatePresignedUrl(generatedUrlRequest).toString()
}

fun ImageView.loadS3Image(key: String, s3client: AmazonS3Client) {
    val url = key.keyToUrl(s3client)
    loadImage(url)
}
