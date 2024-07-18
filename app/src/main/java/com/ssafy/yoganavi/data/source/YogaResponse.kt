package com.ssafy.yoganavi.data.source

data class YogaResponse<T>(
    val data: List<T>,
    val message: String
)
