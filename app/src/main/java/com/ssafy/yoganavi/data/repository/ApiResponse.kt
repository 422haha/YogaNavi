package com.ssafy.yoganavi.data.repository

sealed class ApiResponse<T>(
    val data: List<T>,
    val message: String
) {
    class Success<T>(data: List<T>, message: String) : ApiResponse<T>(data, message)
    class Error<T>(data: List<T> = mutableListOf(), message: String) : ApiResponse<T>(data, message)
}