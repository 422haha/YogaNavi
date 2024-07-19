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

    fun getChapters(recordId: Int) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { repositoryImpl.getLecture(recordId) }
            .onSuccess { it.data?.let { lecture -> _lectureState.emit(lecture) } }
            .onFailure { it.printStackTrace() }
    }

    fun deleteChapter(chapterId: Int) = viewModelScope.launch(Dispatchers.IO) {
        val list = lectureState.value.recordedLectureChapters.toMutableList()
        list.removeIf { it.chapterNumber == chapterId }

        val newLectureState = with(lectureState.value) {
            LectureDetailData(id, recordTitle, recordContent, recordThumbnail, list)
        }
        _lectureState.emit(newLectureState)
    }

    fun addChapter() = viewModelScope.launch(Dispatchers.IO) {
        val list = lectureState.value.recordedLectureChapters.toMutableList()
        val number = list.lastOrNull()?.chapterNumber ?: -1
        list.add(VideoChapterData(chapterNumber = number + 1))

        val newLectureState = with(lectureState.value) {
            LectureDetailData(id, recordTitle, recordContent, recordThumbnail, list)
        }
        _lectureState.emit(newLectureState)
    }
}
