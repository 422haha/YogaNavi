package com.ssafy.yoganavi.ui.utils

fun Array<String>.isBlank() = any { it.isBlank() }
fun Int.toK() = if (this < 1000) {
    toString()
} else if (this >= 100_000) {
    "100K+"
} else {
    "${this / 1000}K+"
}
fun IntToDate(year: Int, month: Int, day: Int): String = run { "$year.${month + 1}.$day" }
fun WeeklyAndTime(weekly: String, timeStr: String): String = run { "$weekly | $timeStr" }

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
const val MANAGEMENT_NOTICE = "공지 관리"
const val MANAGEMENT_INSERT = "공지 생성"
const val MANAGEMENT_UPDATE = "공지 수정"
const val REGISTER_LIVE = "화상 강의 생성"
const val MODIFY_LIVE = "화상 강의 수정"
const val MANAGEMENT_LIVE = "화상 강의 관리"

// 이름 
const val CREATE = "생성"
const val UPDATE = "수정"
const val SAVE = "저장 완료"
const val BUCKET_NAME = "yoga-navi"
const val THUMBNAIL = "thumbnails"
const val NOTICE = "notices"
const val VIDEO = "videos"
const val REGISTER = "등록"
const val EDIT = "편집"
const val DELETE = "삭제"

// 캘린더, 타임 Picker
const val START = 1
const val END = 2

// 화상강의 등록
const val END_STR = "종료 날짜"
const val LIMIT_STR = "무기한"
const val LIMIT_DATE = 4102358400000 // 2099.12.31