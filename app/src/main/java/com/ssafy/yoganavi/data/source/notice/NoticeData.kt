package com.ssafy.yoganavi.data.source.notice

data class NoticeData(
    val articleId: Int,
    val userId: Int,
    val userName: String,
    val content: String,
    val createAt: String,
    val imageUrl: String
)
