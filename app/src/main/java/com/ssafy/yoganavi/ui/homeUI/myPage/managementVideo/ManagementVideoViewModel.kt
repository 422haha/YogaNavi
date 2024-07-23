package com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.source.lecture.LectureData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagementVideoViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {

    private val _lectureList = MutableStateFlow<List<LectureData>>(emptyList())
    val lectureList = _lectureList.asStateFlow()

    fun getLectureList() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getLectureList() }
            .onSuccess { _lectureList.emit(it.data.toMutableList()) }
            .onFailure { it.printStackTrace() }
    }

    fun setLectureLike(recordedId: Long, like: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.likeLecture(recordedId, like) }
            .onSuccess { getLectureList() }
            .onFailure { it.printStackTrace() }
    }

    fun deleteLecture(indexes: List<Int>) = viewModelScope.launch(Dispatchers.IO) {
        val recordedIdList = lectureList.value
            .filterIndexed { index, _ -> indexes.contains(index) }
            .map { it.recordedId }

        runCatching { infoRepository.deleteLectures(recordedIdList) }
            .onSuccess { getLectureList() }
            .onFailure { it.printStackTrace() }
    }
}
