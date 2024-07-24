package com.ssafy.yoganavi.data.repository.response

sealed class ListResponse<T>(
    val data: List<T>,
    val message: String
) {
    class Success<T>(data: List<T>, message: String) : ListResponse<T>(data, message)
    class Error<T>(data: List<T> = mutableListOf(), message: String) : ListResponse<T>(data, message)
    class AuthError<T>(data: List<T> = mutableListOf(), message: String) : ListResponse<T>(data, message)
}