package com.ssafy.yoganavi.data.source.notice

data class NoticeData(
    val articleId: Int = -1,
    val userId: Int = 0,
    val userName: String = "",
    val profileImageUrl: String = "",
    val content: String = "",
    val createdAt: Long = 0L,
    val imageUrl: String = "",
    val updatedAt : Long = 0L
)
