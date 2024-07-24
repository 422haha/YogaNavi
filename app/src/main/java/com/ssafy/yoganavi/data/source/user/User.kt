package com.ssafy.yoganavi.data.source.user

data class User(
    val nickname: String = "",
    val imageUrl: String = "",
    val teacher: Boolean = false,
    val accessToken: String = "",
    val refreshToken: String = ""
)
