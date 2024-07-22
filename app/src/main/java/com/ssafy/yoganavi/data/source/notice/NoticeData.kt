package com.ssafy.yoganavi.data.source.notice

data class NoticeData(
    val articleId: Int = 0,
    val userId: Int = 0,
    val userName: String = "",
    val userProfileImage: String = "",
    val content: String = "",
    val createdAt: String = "",
    val imageUrl: String = ""
)
