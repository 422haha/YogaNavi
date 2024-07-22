package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.ssafy.yoganavi.data.repository.InfoRepositoryImpl
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.lecture.VideoChapterData
import com.ssafy.yoganavi.ui.utils.BUCKET_NAME
import com.ssafy.yoganavi.ui.utils.IS_BLANK
import com.ssafy.yoganavi.ui.utils.NO_RESPONSE
import com.ssafy.yoganavi.ui.utils.THUMBNAIL
import com.ssafy.yoganavi.ui.utils.VIDEO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterVideoViewModel @Inject constructor(
    private val repositoryImpl: InfoRepositoryImpl,
    private val transferUtility: TransferUtility,
    private val s3Client: AmazonS3Client
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

    fun setThumbnail(path: String) = viewModelScope.launch(Dispatchers.IO) {
        val thumbnailKey = "$THUMBNAIL/${UUID.randomUUID()}"
        val recordThumbnail = s3Client.getUrl(BUCKET_NAME, thumbnailKey)

        val newState = lectureState.value.copy(
            recordThumbnail = recordThumbnail.toString(),
            recordThumbnailPath = path,
            thumbnailKey = thumbnailKey
        )
        _lectureState.emit(newState)
    }

    fun setVideo(data: VideoChapterData, path: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val list = lectureState.value.recordedLectureChapters.toMutableList()
            val index = list.indexOfFirst { it == data }
            if (index == -1) return@launch

            val recordKey = "$VIDEO/${UUID.randomUUID()}"
            val recordVideo = s3Client.getUrl(BUCKET_NAME, recordKey)
            list[index] = list[index].copy(
                recordVideo = recordVideo.toString(),
                recordKey = recordKey,
                recordPath = path
            )

            val newState = lectureState.value.copy(recordedLectureChapters = list)
            _lectureState.emit(newState)
        }

    fun sendLecture(
        onSuccess: suspend () -> Unit,
        onFailure: suspend (String) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            val lecture = lectureState.value

            if (anyEmptyLecture(lecture)) {
                onFailure(IS_BLANK)
                return@launch
            }

            val thumbnailFile = File(lecture.recordThumbnailPath)
            transferUtility.upload(BUCKET_NAME, lecture.thumbnailKey, thumbnailFile)

            lecture.recordedLectureChapters.forEach { chapter ->
                val recordFile = File(chapter.recordPath)
                transferUtility.upload(BUCKET_NAME, chapter.recordKey, recordFile)
            }

            // TODO 서버로 강의 정보 전송
        }
            .onSuccess { onSuccess() }
            .onFailure { onFailure(NO_RESPONSE) }
    }

    private fun anyEmptyLecture(lecture: LectureDetailData): Boolean {
        if (lecture.recordThumbnail.isBlank() && lecture.recordThumbnailPath.isBlank()) return true
        if (lecture.recordContent.isBlank() || lecture.recordTitle.isBlank()) return true
        if (lecture.recordedLectureChapters.isEmpty()) return true

        lecture.recordedLectureChapters.forEach { chapter ->
            if (chapter.recordVideo.isBlank() && chapter.recordPath.isBlank()) return true
            if (chapter.chapterTitle.isBlank() || chapter.chapterDescription.isBlank()) return true
        }
        return false
    }
}
