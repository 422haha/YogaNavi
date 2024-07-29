package com.ssafy.yoganavi.data.source.response

data class YogaResponse<T>(
    val data: List<T>,
    val message: String
)
