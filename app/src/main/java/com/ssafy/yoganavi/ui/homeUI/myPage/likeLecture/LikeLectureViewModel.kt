package com.ssafy.yoganavi.ui.homeUI.myPage.likeLecture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikeLectureViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {

    private val _lectureList: MutableStateFlow<List<LectureData>> = MutableStateFlow(emptyList())
    val lectureList: StateFlow<List<LectureData>> = _lectureList.asStateFlow()

    fun getLectureList() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getLikeLectureList() }
            .onSuccess { _lectureList.emit(it.data.toMutableList()) }
            .onFailure { it.printStackTrace() }
    }

    fun setLectureLike(recordedId: Long) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.likeLecture(recordedId) }
            .onSuccess { getLectureList() }
            .onFailure { it.printStackTrace() }
    }


}
