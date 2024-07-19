package com.ssafy.yoganavi.ui.utils

fun Array<String>.isBlank() = any { it.isBlank() }
fun Int.toK() = if (this < 1000) {
    toString()
} else if (this >= 100_000) {
    "100K+"
} else {
    "${this / 1000}K+"
}

// 통신부
const val TIME_OUT = 5000L
const val MEMBER = "members"
const val TOKEN = "Authorization"
const val REFRESH_TOKEN = "Refresh-Token"
const val NEED_REFRESH_TOKEN = "refresh_token_required"
const val TOKEN_REQUIRED = "token_required"

// 수신부
const val NO_RESPONSE = "에러가 발생했습니다."
const val IS_BLANK = "빈칸을 확인해주세요!"
const val PASSWORD_DIFF = "비밀번호가 일치하지 않습니다."
