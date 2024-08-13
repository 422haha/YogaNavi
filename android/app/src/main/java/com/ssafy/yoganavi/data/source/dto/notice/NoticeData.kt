package com.ssafy.yoganavi.data.source.dto.notice

import com.google.gson.annotations.SerializedName

data class NoticeData(
    val articleId: Int = -1,
    val userId: Int = 0,
    val userName: String = "",
    val content: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,

    @SerializedName("profileImageUrl")
    val profileImageKey: String? = "",
    @SerializedName("profileImageSmallUrl")
    val smallProfileImageKey: String? = "",

    @SerializedName("imageUrl")
    val imageKey: String? = "",
    @SerializedName("imageUrlSmall")
    val smallImageKey: String? = "",

    @Transient
    val imageUrlPath: String = "",

    @Transient
    val imageUrlSmallPath: String = "",
)
