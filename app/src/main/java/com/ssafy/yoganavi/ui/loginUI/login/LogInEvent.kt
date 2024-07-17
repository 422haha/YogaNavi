package com.ssafy.yoganavi.ui.loginUI.login

sealed class LogInEvent<T>(
    val data: List<T>,
    val message: String
) {
    class LoginSuccess<T>(data: List<T>, message: String) : LogInEvent<T>(data, message)
    class LoginError<T>(data: List<T> = mutableListOf(), message: String) : LogInEvent<T>(data, message)
}