package com.ssafy.yoganavi.data.source.login

data class LogInData(
    val authorities: List<String>,
    val token: String,
    val username: String
)
