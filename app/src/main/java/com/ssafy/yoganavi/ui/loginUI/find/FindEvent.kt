package com.ssafy.yoganavi.ui.loginUI.find

sealed class FindEvent<T>(
    val data: List<T>,
    val message: String
) {
    class SendEmailSuccess<T>(data: List<T>, message: String) : FindEvent<T>(data, message)
    class CheckEmailSuccess<T>(data: List<T>, message: String) : FindEvent<T>(data, message)
    class RegisterPasswordSuccess<T>(data: List<T>, message: String) : FindEvent<T>(data, message)
    class Error<T>(data: List<T> = mutableListOf(), message: String) : FindEvent<T>(data, message)
}