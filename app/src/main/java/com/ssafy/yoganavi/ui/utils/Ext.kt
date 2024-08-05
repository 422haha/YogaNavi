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
const val FCM_TOKEN = "FCM_Token"
const val REFRESH_TOKEN = "Refresh-Token"
const val NEED_REFRESH_TOKEN = "세션이 만료되었습니다."
const val TOKEN_REQUIRED = "Refresh-Token-Request"
const val PAGE_SIZE = 30
const val END_TIME = "00:00"

// 수신부
const val FORBIDDEN = 403
const val UNAUTHORIZED = 401
const val NO_AUTH = "권한이 없습니다."
const val NO_RESPONSE = "에러가 발생했습니다."
const val IS_BLANK = "빈칸을 확인해주세요!"
const val PASSWORD_DIFF = "비밀번호가 일치하지 않습니다."
const val SESSION_END = "세션이 만료되었습니다."
const val UPLOAD_ERROR = "업로드에 실패했습니다."
const val BLANK_CHAPTER = "강의 영상 하나 이상을 등록해주세요!"

// 타이틀
const val MY_PAGE = "마이 페이지"
const val TEACHER = "요가 강사"
const val FILTER = "필터"
const val REGISTER_VIDEO = "녹화 강의 생성"
const val MANAGEMENT_VIDEO = "녹화 강의 관리"
const val MANAGEMENT_NOTICE = "공지 관리"
const val MANAGEMENT_INSERT = "공지 생성"
const val MANAGEMENT_UPDATE = "공지 수정"
const val REGISTER_LIVE = "화상 강의 생성"
const val MODIFY_LIVE = "화상 강의 수정"
const val MANAGEMENT_LIVE = "화상 강의 관리"
const val TEACHER_DETAIL = "강사"
const val RESERVE = "예약하기"
const val MODIFY = "정보 수정"
const val HOME = "요가 할 일"
const val LIKE_LECTURE = "좋아요한 강의"
const val LIKE_TEACHER = "좋아요한 강사"
const val COURSE_HISTORY = "수강 내역"

// 이름 
const val CREATE = "생성"
const val UPDATE = "수정"
const val SAVE = "저장 완료"
const val YOGA = "yoga"
const val BUCKET_NAME = "yoga-navi"
const val THUMBNAIL = "thumbnails"
const val MINI = "mini"
const val LOGO = "logo"
const val NOTICE = "notices"
const val VIDEO = "videos"
const val REGISTER = "등록"
const val EDIT = "편집"
const val DELETE = "삭제"
const val RESERVATION = "예약"
const val MAX_HASH_TAG = 5
const val IS_MAX_HASH_TAG = "해시태그는 5개까지만 등록하실 수 있습니다."
const val LECTURE_LIST = "강의 찾기"
const val LECTURE = "강의"
const val FAME = "fame"
const val DATE = "date"
const val ANY_CHECK_BOX = "찾을 파트를 선택해주세요!"
const val SELECT_CLASS = "수업방식을 선택하세요."
const val NOTHING = "수강가능한 수업이 없습니다."
const val PICK_DATE = "날짜를 선택하세요."
const val UPLOAD_FAIL = "업로드에 실패했습니다."

// empty 관리
const val EMPTY_LIVE = "예정된 실시간 강의가 없습니다 \uD83D\uDE0C"
const val GO_TEACHER = "수강신청 하러가기"
const val EMPTY_NOTICE = "아직 등록된 공지가 없습니다 \uD83D\uDE0C"
const val EMPTY_LECTURE = "아직 등록된 강의가 없습니다 \uD83D\uDE0C"
const val EMPTY_MY_LIVE = "아직 생성된 실시간 강의가 없습니다 \uD83D\uDE0C"
const val EMPTY_MY_LECTURE = "아직 생성된 녹화 강의가 없습니다 \uD83D\uDE0C"
const val EMPTY_TEACHER = "아직 등록된 선생님이 없습니다 \uD83D\uDE0C"
const val EMPTY_LIKE_LECTURE = "아직 좋아요 표시한 강의가 없습니다 \uD83D\uDE0C"
const val GO_LIKE_LECTURE = "강의 좋아요 하러 가기"
const val EMPTY_LIKE_TEACHER = "아직 좋아요 표시한 강사가 없습니다.\uD83D\uDE0C"
const val EMPTY_COURSE = "아직 수강한 실시간 강의가 없습니다 \uD83D\uDE0C"


//강의 분류
const val ONE_TO_ONE = 0
const val ONE_TO_MULTI = 1
const val RECENT = 0
const val POPULAR = 1
const val PERIOD_WEEK = 0
const val PERIOD_MONTH = 1
const val PERIOD_THREE_MONTH = 2
const val PERIOD_TOTAL = 3


// 캘린더, 타임 Picker
const val START = 1
const val END = 2

// 강의 Header, Item
const val HEADER = 1
const val ITEM = 2
const val ITEM_LECTURE = 2
const val ITEM_NOTICE = 3

// 화상강의 등록
const val END_STR = "종료 날짜"

enum class Week(val hangle: String) {
    MON("월"),
    TUE("화"),
    WED("수"),
    THU("목"),
    FRI("금"),
    SAT("토"),
    SUN("일")
}

data class DayStatus(val day: Week, var isSelected: Boolean)

// FCM
const val CHANNEL_ID = "YogaNaviChannelId"
const val CHANNEL_NAME = "YogaNavi"
const val CHANNEL_DESCRIPTION = "YogaNavi Live Notification"


// 에러 모음집
const val IS_NOT_EMAIL = "유효하지 않은 이메일 형식입니다."
const val NOT_USER = "존재하지 않는 회원입니다."
const val MAX_NAME = 20
const val NAME_IS_MAX = "닉네임은 20자까지만 가능합니다"