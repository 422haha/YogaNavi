package com.ssafy.yoganavi.data.source.signup

data class SignUpRequest(
    val email: String? = null,
    val password: String? = null,
    val nickname: String? = null,
    val authNumber: Int = 0,
    val teacher: Boolean = false
)
