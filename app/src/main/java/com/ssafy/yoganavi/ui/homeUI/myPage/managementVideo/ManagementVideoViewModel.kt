package com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo

import androidx.lifecycle.ViewModel
import com.ssafy.yoganavi.data.source.lecture.LectureData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ManagementVideoViewModel : ViewModel() {

    private val _lectureList = MutableStateFlow<List<LectureData>>(emptyList())
    val lectureList = _lectureList.asStateFlow()

    fun getLectureList() {
        test()
    }

    private fun test() {
        val list = mutableListOf<LectureData>()
        repeat(10) {
            val lecture = LectureData(
                recordedId = it.toString(),
                recordedTitle = "test",
                recordedThumbnail = "https://img.khan.co.kr/news/2024/03/23/news-p.v1.20240323.c159a4cab6f64473adf462d873e01e43_P1.jpg",
                likes = it,
                likedByUser = it % 2 == 0
            )

            list.add(lecture)
        }
        _lectureList.value = list.toMutableList()
    }
}
