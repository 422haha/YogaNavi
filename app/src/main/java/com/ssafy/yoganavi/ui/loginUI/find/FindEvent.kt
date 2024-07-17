package com.ssafy.yoganavi.ui.loginUI.find

sealed class FindEvent<T>(
    val data: T? = null,
    val message: String? = null
) {
    class SendEmailSuccess<T>(data: T) : FindEvent<T>(data)
    class CheckEmailSuccess<T>(data: T) : FindEvent<T>(data)
    class RegisterPasswordSuccess<T>(data: T) : FindEvent<T>(data)
    class Error<T>(message: String, data: T? = null) : FindEvent<T>(data, message)
}