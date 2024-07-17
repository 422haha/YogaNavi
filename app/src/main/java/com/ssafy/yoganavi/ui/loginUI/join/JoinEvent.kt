package com.ssafy.yoganavi.ui.loginUI.join

sealed class JoinEvent<T>(
    val data: T? = null,
    val message: String? = null
) {
    class RegisterEmailSuccess<T>(data: T) : JoinEvent<T>(data)
    class CheckEmailSuccess<T>(data: T) : JoinEvent<T>(data)
    class SignUpSuccess<T>(data: T) : JoinEvent<T>(data)
    class Error<T>(message: String, data: T? = null) : JoinEvent<T>(data, message)
}