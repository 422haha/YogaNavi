package com.ssafy.yoganavi.ui.utils

fun Array<String>.isBlank() = any { it.isBlank() }

const val TIME_OUT = 5000L
const val CHECK = "확인"
const val CERTIFICATION = "인증"
const val NO_RESPONSE = "에러 발생"
const val IS_BLANK = "빈칸을 확인해주세요!"
const val PASSWORD_DIFF = "비밀번호가 일치하지 않습니다."
