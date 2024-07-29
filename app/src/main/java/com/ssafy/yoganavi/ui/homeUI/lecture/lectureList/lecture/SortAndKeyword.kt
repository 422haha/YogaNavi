package com.ssafy.yoganavi.ui.homeUI.lecture.lectureList.lecture

import com.ssafy.yoganavi.ui.utils.DATE

data class SortAndKeyword(
    val sort: String = DATE,
    val keyword: String? = null,
    val searchInTitle: Boolean = true,
    val searchInContent: Boolean = true
)
