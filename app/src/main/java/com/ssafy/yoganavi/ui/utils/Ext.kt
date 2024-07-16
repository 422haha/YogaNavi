package com.ssafy.yoganavi.ui.utils

fun Array<String>.isBlank() = any { it.isBlank() }

const val TIME_OUT = 5000L
const val NO_RESPONSE = "에러 발생"
const val IS_BLANK = "빈칸을 확인해주세요!"
