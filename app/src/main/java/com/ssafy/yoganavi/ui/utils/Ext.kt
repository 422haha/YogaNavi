package com.ssafy.yoganavi.ui.utils

fun Array<String>.isBlank() = any { it.isBlank() }

const val TIME_OUT = 5000L
const val MEMBER = "members"
const val NO_RESPONSE = "에러가 발생했습니다."
const val IS_BLANK = "빈칸을 확인해주세요!"
const val PASSWORD_DIFF = "비밀번호가 일치하지 않습니다."
