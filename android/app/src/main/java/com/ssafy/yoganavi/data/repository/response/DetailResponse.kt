package com.ssafy.yoganavi.data.repository.response

sealed class DetailResponse<T>(
    val data: T?,
    val message: String
) {
    class Success<T>(data: T, message: String) : DetailResponse<T>(data, message)
    class Error<T>(data: T? = null, message: String) : DetailResponse<T>(data, message)
    class AuthError<T>(data: T? = null, message: String) : DetailResponse<T>(data, message)
}