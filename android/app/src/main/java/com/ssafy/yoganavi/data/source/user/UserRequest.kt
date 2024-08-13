package com.ssafy.yoganavi.data.source.user

data class UserRequest(
    val email: String = "",
    val password: String = "",
    val nickname: String? = null,
    val authnumber: Int = 0,
    val teacher: Boolean = false
)
