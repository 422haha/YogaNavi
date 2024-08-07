package com.ssafy.yoganavi.data.source.dto.mypage

import com.google.gson.annotations.SerializedName

data class Profile(
    val nickname: String = "",
    val password: String = "",
    val hashTags: List<String>? = emptyList(),
    val teacher: Boolean = false,
    val content: String? = "",

    @SerializedName("imageUrl")
    val imageKey: String? = "",

    @SerializedName("imageUrlSmall")
    val smallImageKey: String? = "",

    @Transient
    val logoPath: String = "",

    @Transient
    val logoSmallPath: String = "",
)