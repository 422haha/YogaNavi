package com.ssafy.yoganavi.data.source.notice

data class RegisterNoticeRequest(
    val title: String,
    val content : String,
    val createdAt: String,
    val imageUrl: String
)
