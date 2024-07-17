package com.ssafy.yoganavi.data.source.user.signup

data class SignUpRequest(
    val email: String? = null,
    val password: String? = null,
    val nickname: String? = null,
    val authnumber: Int = 0,
    val teacher: Boolean = false
)
