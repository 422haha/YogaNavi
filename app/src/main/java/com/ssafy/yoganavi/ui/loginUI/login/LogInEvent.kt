package com.ssafy.yoganavi.ui.loginUI.login

sealed class LogInEvent(
    val data: Boolean,
    val message: String
) {
    class LoginSuccess(data: Boolean, message: String) : LogInEvent(data, message)
    class LoginError(data: Boolean = false, message: String) : LogInEvent(data, message)
}