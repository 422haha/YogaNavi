package com.ssafy.yoganavi.data.source.user

data class User(
    val email: String = "",
    val password: String = "",
    val nickname: String = "",
    val teacher: Boolean = false,
    val accessToken: String = "",
    val refreshToken: String = ""
)
