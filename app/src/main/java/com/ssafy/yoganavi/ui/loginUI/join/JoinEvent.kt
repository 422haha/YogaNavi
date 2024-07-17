package com.ssafy.yoganavi.ui.loginUI.join

sealed class JoinEvent<T>(
    val data: List<T>,
    val message: String
) {
    class RegisterEmailSuccess<T>(data: List<T>, message: String) : JoinEvent<T>(data, message)
    class CheckEmailSuccess<T>(data: List<T>, message: String) : JoinEvent<T>(data, message)
    class SignUpSuccess<T>(data: List<T>, message: String) : JoinEvent<T>(data, message)
    class Error<T>(data: List<T> = mutableListOf(), message: String) : JoinEvent<T>(data, message)
}