package com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.repository.response.AuthException
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
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

    fun getLectureList(endSession: suspend () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getLectureList() }
            .onSuccess { _lectureList.emit(it.data.toMutableList()) }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }

    fun setLectureLike(
        recordedId: Long,
        endSession: suspend () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.likeLecture(recordedId) }
            .onSuccess { getLectureList(endSession) }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }

    fun deleteLecture(
        indexes: List<Int>,
        endSession: suspend () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        val recordedIdList = lectureList.value
            .filterIndexed { index, _ -> indexes.contains(index) }
            .map { it.recordedId }

        runCatching { infoRepository.deleteLectures(recordedIdList) }
            .onSuccess { getLectureList(endSession) }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }
}
