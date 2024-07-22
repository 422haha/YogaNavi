package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.InfoRepositoryImpl
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.lecture.VideoChapterData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterVideoViewModel @Inject constructor(
    private val repositoryImpl: InfoRepositoryImpl
) : ViewModel() {

    private val _lectureState = MutableStateFlow(LectureDetailData())
    val lectureState: StateFlow<LectureDetailData> = _lectureState.asStateFlow()

    fun getLecture(recordId: Int) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { repositoryImpl.getLecture(recordId) }
            .onSuccess { it.data?.let { lecture -> _lectureState.emit(lecture) } }
            .onFailure { it.printStackTrace() }
    }

    fun deleteChapter(data: VideoChapterData) = viewModelScope.launch(Dispatchers.IO) {
        val list = lectureState.value.recordedLectureChapters.toMutableList()
        list.remove(data)
        val newState = lectureState.value.copy(recordedLectureChapters = list)
        _lectureState.emit(newState)
    }

    fun addChapter() = viewModelScope.launch(Dispatchers.IO) {
        val list = lectureState.value.recordedLectureChapters.toMutableList()
        list.add(VideoChapterData())
        val newState = lectureState.value.copy(recordedLectureChapters = list)
        _lectureState.emit(newState)
    }

    fun setVideo(data: VideoChapterData, videoUri: String) = viewModelScope.launch(Dispatchers.IO) {
        val list = lectureState.value.recordedLectureChapters.toMutableList()
        val index = list.indexOfFirst { it == data }
        if(index == -1) return@launch

        list[index] = list[index].copy(videoUrl = videoUri)
        val newState = lectureState.value.copy(recordedLectureChapters = list)
        _lectureState.emit(newState)
    }
}
