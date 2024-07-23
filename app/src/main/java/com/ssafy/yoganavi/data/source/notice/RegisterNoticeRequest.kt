package com.ssafy.yoganavi.data.source.notice

data class RegisterNoticeRequest(
    val content: String = "",
    val imageUrl: String? = null
)
