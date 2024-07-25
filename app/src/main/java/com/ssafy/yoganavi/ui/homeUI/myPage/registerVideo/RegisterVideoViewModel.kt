package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.repository.response.DetailResponse
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.lecture.VideoChapterData
import com.ssafy.yoganavi.ui.utils.BUCKET_NAME
import com.ssafy.yoganavi.ui.utils.IS_BLANK
import com.ssafy.yoganavi.ui.utils.NO_AUTH
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
    private val infoRepository: InfoRepository,
    private val transferUtility: TransferUtility,
    private val s3Client: AmazonS3Client
) : ViewModel() {

    private val _chapterList: MutableStateFlow<List<VideoChapterData>> =
        MutableStateFlow(mutableListOf())
    val chapterList: StateFlow<List<VideoChapterData>> = _chapterList.asStateFlow()

    private var lectureDetailData = LectureDetailData()

    fun getLecture(recordId: Long, setView: suspend (LectureDetailData) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { infoRepository.getLecture(recordId) }
                .onSuccess {
                    it.data?.let { lecture ->
                        setView(lecture)
                        lectureDetailData = lecture
                        _chapterList.emit(lecture.recordedLectureChapters)
                    }
                }
                .onFailure { it.printStackTrace() }
        }

    fun deleteChapter(data: VideoChapterData) = viewModelScope.launch(Dispatchers.IO) {
        val list = chapterList.value.toMutableList()
        list.remove(data)
        _chapterList.emit(list)
    }

    fun addChapter() = viewModelScope.launch(Dispatchers.IO) {
        val list = chapterList.value.toMutableList()
        list.add(VideoChapterData())
        _chapterList.emit(list)
    }

    fun setThumbnail(path: String) = viewModelScope.launch(Dispatchers.IO) {
        val thumbnailKey = "$THUMBNAIL/${UUID.randomUUID()}"
        val recordThumbnail = s3Client.getUrl(BUCKET_NAME, thumbnailKey)

        lectureDetailData = lectureDetailData.copy(
            recordThumbnail = recordThumbnail.toString(),
            recordThumbnailPath = path,
            thumbnailKey = thumbnailKey
        )
    }

    fun setVideo(data: VideoChapterData, path: String) = viewModelScope.launch(Dispatchers.IO) {
        val list = chapterList.value.toMutableList()
        val index = list.indexOfFirst { it == data }
        if (index == -1) return@launch
        val recordKey = "$VIDEO/${UUID.randomUUID()}"
        val recordVideo = s3Client.getUrl(BUCKET_NAME, recordKey)
        list[index] = list[index].copy(
            recordVideo = recordVideo.toString(),
            recordKey = recordKey,
            recordPath = path
        )
        _chapterList.emit(list)
    }

    fun sendLecture(
        id: Long,
        lectureTitle: String,
        lectureContent: String,
        titleList: List<String>,
        contentList: List<String>,
        onSuccess: suspend () -> Unit,
        onFailure: suspend (String) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {

            lectureDetailData = lectureDetailData.copy(
                recordedId = id,
                recordTitle = lectureTitle,
                recordContent = lectureContent
            )

            if (titleList.size != contentList.size || titleList.size != lectureDetailData.recordedLectureChapters.size) {
                onFailure(IS_BLANK)
                return@launch
            }

            val chapterList = mutableListOf<VideoChapterData>()
            for (index in titleList.indices) {
                val data = VideoChapterData(
                    chapterTitle = titleList[index],
                    chapterDescription = contentList[index],
                    recordVideo = lectureDetailData.recordedLectureChapters[index].recordVideo,
                    recordKey = lectureDetailData.recordedLectureChapters[index].recordKey,
                    recordPath = lectureDetailData.recordedLectureChapters[index].recordPath
                )
                chapterList.add(data)
            }
            lectureDetailData = lectureDetailData.copy(recordedLectureChapters = chapterList)

            if (anyEmptyLecture(lectureDetailData)) {
                onFailure(IS_BLANK)
                return@launch
            }

            if (lectureDetailData.recordThumbnailPath.isNotBlank()) {
                val thumbnailFile = File(lectureDetailData.recordThumbnailPath)
                val metadata = ObjectMetadata().apply { contentType = "image/webp" }
                transferUtility.upload(
                    BUCKET_NAME,
                    lectureDetailData.thumbnailKey,
                    thumbnailFile,
                    metadata
                )
            } else {
                val thumbnailUrl = lectureDetailData.recordThumbnail.substringBefore("?")
                lectureDetailData = lectureDetailData.copy(recordThumbnail = thumbnailUrl)
            }

            lectureDetailData.recordedLectureChapters.forEachIndexed { index, chapter ->
                if (chapter.recordPath.isNotBlank()) {
                    val recordFile = File(chapter.recordPath)
                    transferUtility.upload(BUCKET_NAME, chapter.recordKey, recordFile)
                } else {
                    val videoUrl = chapter.recordVideo.substringBefore("?")
                    val newChapter = chapter.copy(recordVideo = videoUrl)
                    lectureDetailData.recordedLectureChapters[index] = newChapter
                }
            }

            val response = if (id == -1L) {
                infoRepository.createLecture(lectureDetailData)
            } else {
                infoRepository.updateLecture(lectureDetailData)
            }

            if (response is DetailResponse.AuthError) {
                onFailure(NO_AUTH)
                return@launch
            } else if (response is DetailResponse.Error) {
                onFailure(NO_RESPONSE)
                return@launch
            }
        }
            .onSuccess { onSuccess() }
            .onFailure { onFailure(NO_RESPONSE) }
    }

    fun setChapterList(list: MutableList<VideoChapterData>) {
        lectureDetailData = lectureDetailData.copy(recordedLectureChapters = list)
    }

    private fun anyEmptyLecture(lecture: LectureDetailData): Boolean {
        if (lecture.recordContent.isBlank() || lecture.recordTitle.isBlank() || lecture.recordThumbnail.isBlank()) return true
        if (lecture.recordedLectureChapters.isEmpty()) return true

        lecture.recordedLectureChapters.forEach { chapter ->
            if (chapter.chapterTitle.isBlank() || chapter.chapterDescription.isBlank() || chapter.recordVideo.isBlank()) return true
        }
        return false
    }
}
