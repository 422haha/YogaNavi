package com.ssafy.yoganavi.data.source.mypage

data class Profile(
    val nickname: String = "",
    val password: String = "",
    val imageUrl: String? = "",
    val hashTags: List<String> = emptyList(),
    val teacher: Boolean = false,

    @Transient
    val logoPath: String = "",
    @Transient
    val logoKey: String = ""
)
