package com.ssafy.yoganavi.ui.loginUI.login

sealed class LogInEvent<T>(
    val data: T? = null,
    val message: String? = null
) {
    class LoginSuccess<T>(data: T) : LogInEvent<T>(data)
    class LoginError<T>(message: String, data: T? = null) : LogInEvent<T>(data, message)
}