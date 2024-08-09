package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.repository.response.DetailResponse
import com.ssafy.yoganavi.data.source.dto.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.dto.lecture.VideoChapterData
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data.ChapterItem
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data.ThumbnailData
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.data.VideoData
import com.ssafy.yoganavi.ui.utils.BLANK_CHAPTER
import com.ssafy.yoganavi.ui.utils.MINI
import com.ssafy.yoganavi.ui.utils.NO_AUTH
import com.ssafy.yoganavi.ui.utils.NO_RESPONSE
import com.ssafy.yoganavi.ui.utils.THUMBNAIL
import com.ssafy.yoganavi.ui.utils.VIDEO
import com.ssafy.yoganavi.ui.utils.keyToUrl
import com.ssafy.yoganavi.ui.utils.loadS3ImageSequentially
import com.ssafy.yoganavi.ui.utils.loadS3VideoFrame
import com.ssafy.yoganavi.ui.utils.uploadFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    private val _chapterList: MutableStateFlow<List<ChapterItem>> = MutableStateFlow(initList())
    val chapterList: StateFlow<List<ChapterItem>> = _chapterList.asStateFlow()

    fun getLecture(recordId: Long) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getMypageLecture(recordId) }
            .onSuccess { it.data?.let { data -> _chapterList.emit(data.toChapterItem()) } }
            .onFailure { it.printStackTrace() }
    }

    fun deleteChapter(position: Int) = viewModelScope.launch(Dispatchers.IO) {
        val list = chapterList.value.toMutableList()
        list.removeAt(position)
        _chapterList.emit(list)
    }

    fun addChapter() = viewModelScope.launch(Dispatchers.IO) {
        val list = chapterList.value.toMutableList()
        list.add(ChapterItem.VideoItem(VideoData(list.last().id + 1L)))
        _chapterList.emit(list)
    }

    fun setVideo(position: Int, path: String) = viewModelScope.launch(Dispatchers.IO) {
        val recordKey = "$VIDEO/${UUID.randomUUID()}"
        val list = chapterList.value.toMutableList()
        val videoItem = list[position] as ChapterItem.VideoItem
        val originalVideoData = videoItem.videoData

        val newVideoData = originalVideoData.copy(
            recordKey = recordKey,
            recordPath = path
        ).apply {
            chapterTitle = originalVideoData.chapterTitle
            chapterDescription = originalVideoData.chapterDescription
        }
        list[position] = ChapterItem.VideoItem(newVideoData)
        _chapterList.emit(list)
    }

    fun setThumbnail(path: String, miniPath: String) = viewModelScope.launch(Dispatchers.IO) {
        val uuid = UUID.randomUUID()
        val imageKey = "$THUMBNAIL/${uuid}"
        val smallImageKey = "$THUMBNAIL/$MINI/${uuid}"
        val newChapterList = chapterList.value.toMutableList()
        val thumbnailItem = newChapterList[0] as ChapterItem.ImageItem
        val originalThumbnailData = thumbnailItem.thumbnailData

        val newThumbnailData = originalThumbnailData.copy(
            recordThumbnailPath = path,
            miniThumbnailPath = miniPath,
            imageKey = imageKey,
            smallImageKey = smallImageKey
        ).apply {
            recordTitle = originalThumbnailData.recordTitle
            recordContent = originalThumbnailData.recordContent
        }
        newChapterList[0] = ChapterItem.ImageItem(newThumbnailData)
        _chapterList.emit(newChapterList)
    }

    fun setThumbnailTitle(title: String?) = viewModelScope.launch(Dispatchers.IO) {
        if (title == null) return@launch
        (chapterList.value[0] as ChapterItem.ImageItem).thumbnailData.recordTitle = title
    }

    fun setThumbnailContent(content: String?) = viewModelScope.launch(Dispatchers.IO) {
        if (content == null) return@launch
        (chapterList.value[0] as ChapterItem.ImageItem).thumbnailData.recordContent = content
    }

    fun setVideoTitle(title: String?, position: Int) = viewModelScope.launch(Dispatchers.IO) {
        if (title == null) return@launch
        (chapterList.value[position] as ChapterItem.VideoItem).videoData.chapterTitle = title
    }

    fun setVideoContent(content: String?, position: Int) = viewModelScope.launch(Dispatchers.IO) {
        if (content == null) return@launch
        (chapterList.value[position] as ChapterItem.VideoItem).videoData.chapterDescription =
            content
    }

    fun makeLecture(
        recordId: Long,
        loadingView: suspend () -> Unit,
        successToUpload: suspend () -> Unit,
        failToUpload: suspend (String) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            loadingView()

            if (isChapterEmpty()) {
                failToUpload(BLANK_CHAPTER)
                return@launch
            }

            val lectureDetailData: LectureDetailData = chapterList.value.toLectureDetail()
            val deferredList = mutableListOf<Deferred<Boolean>>()

            if (lectureDetailData.recordThumbnailPath.isNotBlank()) {
                val thumbnailFile = File(lectureDetailData.recordThumbnailPath)
                val miniFile = File(lectureDetailData.miniThumbnailPath)
                val metadata = ObjectMetadata().apply { contentType = "image/webp" }
                val imageKey = lectureDetailData.imageKey
                val smallImageKey = lectureDetailData.smallImageKey

                val uploadImage =
                    async { uploadFile(transferUtility, imageKey, thumbnailFile, metadata) }
                val uploadMiniImage =
                    async { uploadFile(transferUtility, smallImageKey, miniFile, metadata) }

                deferredList.add(uploadImage)
                deferredList.add(uploadMiniImage)
            }

            lectureDetailData.recordedLectureChapters.forEach { chapter ->
                if (chapter.recordPath.isBlank()) return@forEach
                val recordFile = File(chapter.recordPath)
                val uploadVideo =
                    async { uploadFile(transferUtility, chapter.recordKey, recordFile) }
                deferredList.add(uploadVideo)
            }

            val result = deferredList.awaitAll()
            if (result.any { false }) {
                failToUpload(NO_RESPONSE)
                return@launch
            }

            val response = if (recordId == -1L) {
                infoRepository.createLecture(lectureDetailData)
            } else {
                infoRepository.updateLecture(lectureDetailData)
            }

            if (response is DetailResponse.AuthError) {
                failToUpload(NO_AUTH)
                return@launch
            } else if (response is DetailResponse.Error) {
                failToUpload(NO_RESPONSE)
                return@launch
            }
        }
            .onSuccess { successToUpload() }
            .onFailure { failToUpload(NO_RESPONSE) }

    }

    private fun isChapterEmpty(): Boolean {
        val chapterItemList = chapterList.value
        val isEmpty = chapterItemList
            .filterIsInstance<ChapterItem.VideoItem>()
            .ifEmpty { return true }
            .map { it.videoData }
            .any {
                it.chapterTitle.isNullOrBlank() || it.chapterDescription.isNullOrBlank() || it.recordKey.isBlank()
            }

        if (isEmpty) return true

        val thumbnailData = (chapterItemList[0] as ChapterItem.ImageItem).thumbnailData
        return thumbnailData.recordTitle.isNullOrBlank() || thumbnailData.recordContent.isNullOrBlank()
    }

    private fun LectureDetailData.toChapterItem(): List<ChapterItem> {
        val itemList = mutableListOf<ChapterItem>()
        val thumbnailData = ThumbnailData(
            recordedId = recordedId,
            recordThumbnailPath = recordThumbnailPath,
            miniThumbnailPath = miniThumbnailPath,
            imageKey = imageKey,
            smallImageKey = smallImageKey
        ).apply {
            recordTitle = this@toChapterItem.recordTitle
            recordContent = this@toChapterItem.recordContent
        }

        itemList.add(ChapterItem.ImageItem(thumbnailData))

        recordedLectureChapters.forEach {
            val videoData = VideoData(
                id = it.id,
                recordPath = it.recordPath,
                recordKey = it.recordKey,
                chapterNumber = it.chapterNumber
            ).apply {
                chapterTitle = it.chapterTitle
                chapterDescription = it.chapterDescription
            }
            itemList.add(ChapterItem.VideoItem(videoData))
        }

        return itemList
    }

    private fun List<ChapterItem>.toLectureDetail(): LectureDetailData {
        val thumbnailData = (this[0] as ChapterItem.ImageItem).thumbnailData
        val recordedLectureChapters = this.asSequence().drop(1)
            .map { it as ChapterItem.VideoItem }
            .map {
                val videoData = it.videoData
                VideoChapterData(
                    id = if (videoData.recordPath.isNotBlank()) 0 else videoData.id,
                    chapterNumber = videoData.chapterNumber,
                    chapterTitle = videoData.chapterTitle ?: "",
                    chapterDescription = videoData.chapterDescription ?: "",
                    recordPath = videoData.recordPath,
                    recordKey = videoData.recordKey
                )
            }.toMutableList()

        return LectureDetailData(
            recordedId = thumbnailData.recordedId,
            recordTitle = thumbnailData.recordTitle ?: "",
            recordContent = thumbnailData.recordContent ?: "",
            recordedLectureChapters = recordedLectureChapters,
            recordThumbnailPath = thumbnailData.recordThumbnailPath,
            miniThumbnailPath = thumbnailData.miniThumbnailPath,
            imageKey = thumbnailData.imageKey,
            smallImageKey = thumbnailData.smallImageKey
        )
    }

    private fun initList(): List<ChapterItem> {
        val thumbnailData = ThumbnailData()
        return listOf(ChapterItem.ImageItem(thumbnailData))
    }

    fun loadS3ImageSequentially(view: ImageView, smallKey: String, largeKey: String) =
        view.loadS3ImageSequentially(smallKey, largeKey, s3Client)

    fun loadS3VideoFrame(view: ImageView, key: String, time: Long, circularOn: Boolean) =
        view.loadS3VideoFrame(key, time, circularOn, s3Client)

    fun makeUrlFromKey(key: String): String = key.keyToUrl(s3Client)
}
