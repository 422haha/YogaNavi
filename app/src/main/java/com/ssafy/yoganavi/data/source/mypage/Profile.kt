package com.ssafy.yoganavi.data.source.mypage

import com.google.gson.annotations.Expose

data class Profile(
    val nickname: String = "",
    val password: String = "",
    val imageUrl: String? = "",
    val hashTags: List<String> = emptyList(),

    @Expose(serialize = false)
    val teacher: Boolean = false,

    @Transient
    val logoPath: String = "",
    @Transient
    val logoKey: String = ""
)
