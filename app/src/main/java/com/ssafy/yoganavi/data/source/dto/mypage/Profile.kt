package com.ssafy.yoganavi.data.source.dto.mypage

data class Profile(
    val nickname: String = "",
    val password: String = "",
    val imageUrl: String? = "",
    val imageUrlSmall: String? = "",
    val hashTags: List<String>? = emptyList(),
    val teacher: Boolean = false,
    val content: String? = "",

    @Transient
    val logoPath: String = "",
    @Transient
    val logoKey: String = "",

    @Transient
    val logoSmallPath: String = "",
    @Transient
    val logoSmallKey: String = ""
)