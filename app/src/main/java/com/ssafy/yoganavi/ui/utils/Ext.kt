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
const val FORBIDDEN = 403
const val NO_AUTH = "권한이 없습니다."
const val NO_RESPONSE = "에러가 발생했습니다."
const val IS_BLANK = "빈칸을 확인해주세요!"
const val PASSWORD_DIFF = "비밀번호가 일치하지 않습니다."

// 타이틀
const val MY_PAGE = "마이 페이지"
const val REGISTER_VIDEO = "녹화 강의 생성"
const val MANAGEMENT_VIDEO = "녹화 강의 관리"

// 이름 
const val CREATE = "생성"