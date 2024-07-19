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
            .onSuccess { _lectureList.emit(it.data) }
            .onFailure { it.printStackTrace() }
    }

    fun setLectureLike(likes: Int) = viewModelScope.launch(Dispatchers.IO) {

    }
}
