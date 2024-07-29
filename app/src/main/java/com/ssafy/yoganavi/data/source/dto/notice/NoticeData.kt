package com.ssafy.yoganavi.data.source.dto.notice

data class NoticeData(
    val articleId: Int = -1,
    val userId: Int = 0,
    val userName: String = "",
    val profileImageUrl: String? = "",
    val profileImageSmallUrl: String? = "",
    val content: String = "",
    val createdAt: Long = 0L,
    val imageUrl: String? = "",
    val imageUrlSmall: String? = "",
    val updatedAt: Long = 0L,

    @Transient
    val imageUrlPath: String = "",
    @Transient
    val imageUrlKey: String = "",

    @Transient
    val imageUrlSmallPath: String = "",
    @Transient
    val imageUrlSmallKey: String = ""
)
