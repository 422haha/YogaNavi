package com.ssafy.yoganavi.data.source.login

data class LogInResponse(
    val data: LogInData?,
    val message: String,
    val status: String,
    val errorCode: Int
)
