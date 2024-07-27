package com.ssafy.yoganavi.ui.homeUI.myPage.registerNotice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.source.notice.NoticeData
import com.ssafy.yoganavi.data.source.notice.RegisterNoticeRequest
import com.ssafy.yoganavi.ui.utils.BUCKET_NAME
import com.ssafy.yoganavi.ui.utils.MINI
import com.ssafy.yoganavi.ui.utils.NOTICE
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
class RegisterNoticeViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val transferUtility: TransferUtility,
    private val s3Client: AmazonS3Client
) : ViewModel() {
    private val _notice = MutableStateFlow(NoticeData())
    val notice: StateFlow<NoticeData> = _notice.asStateFlow()

    fun getNotice(articleId: Int) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getNotice(articleId) }
            .onSuccess {
                it.data?.let { notice -> _notice.emit(notice) }
            }
            .onFailure { it.printStackTrace() }
    }

    fun removeImage() = viewModelScope.launch(Dispatchers.IO) {
        val newNotice = notice.value.copy(
            imageUrl = "",
            imageUrlPath = "",
            imageUrlKey = "",
            imageUrlSmall = "",
            imageUrlSmallPath = "",
            imageUrlSmallKey = ""
        )
        _notice.emit(newNotice)
    }

    fun addImage(path: String, smallPath: String) = viewModelScope.launch(Dispatchers.IO) {
        val newNotice = notice.value.copy(
            imageUrl = "",
            imageUrlPath = path,
            imageUrlKey = "",
            imageUrlSmall = "",
            imageUrlSmallPath = smallPath,
            imageUrlSmallKey = ""
        )
        _notice.emit(newNotice)
    }

    fun setContent(content: String) = viewModelScope.launch(Dispatchers.IO) {
        _notice.emit(notice.value.copy(content = content))
    }

    fun insertNotice(content: String, goBackStack: suspend () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val uuid = UUID.randomUUID()
            val imageUrlKey = "$NOTICE/$uuid"
            val imageUrlSmallKey = "$NOTICE/$MINI/$uuid"
            val imageUrl = s3Client.getUrl(BUCKET_NAME, imageUrlKey)
            val imageUrlSmall = s3Client.getUrl(BUCKET_NAME, imageUrlSmallKey)

            val noticeFile = File(notice.value.imageUrlPath)
            val miniFile = File(notice.value.imageUrlSmallPath)

            val metadata = ObjectMetadata().apply { contentType = "image/webp" }
            transferUtility.upload(BUCKET_NAME, imageUrlKey, noticeFile, metadata)
            transferUtility.upload(BUCKET_NAME, imageUrlSmallKey, miniFile, metadata)

            val request = RegisterNoticeRequest(
                content = content,
                imageUrl = imageUrl.toString(),
                imageUrlSmall = imageUrlSmall.toString()
            )

            runCatching { infoRepository.insertNotice(request) }
                .onSuccess { goBackStack() }
                .onFailure { it.printStackTrace() }
        }

    fun updateNotice(content: String, goBackStack: suspend () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val uuid = UUID.randomUUID()
            val imageUrlKey = "$NOTICE/$uuid"
            val imageUrlSmallKey = "$NOTICE/$MINI/$uuid"
            val imageUrl = s3Client.getUrl(BUCKET_NAME, imageUrlKey)
            val imageUrlSmall = s3Client.getUrl(BUCKET_NAME, imageUrlSmallKey)

            if (notice.value.imageUrlPath.isNotBlank()) {
                val noticeFile = File(notice.value.imageUrlPath)
                val miniFile = File(notice.value.imageUrlSmallPath)
                val metadata = ObjectMetadata().apply { contentType = "image/webp" }

                transferUtility.upload(BUCKET_NAME, imageUrlKey, noticeFile, metadata)
                transferUtility.upload(BUCKET_NAME, imageUrlSmallKey, miniFile, metadata)

                val newNotice = notice.value.copy(
                    imageUrl = imageUrl.toString(),
                    imageUrlSmall = imageUrlSmall.toString(),
                )
                _notice.emit(newNotice)
            } else {
                val prevUrl = notice.value.imageUrl?.substringBefore("?")
                val prevUrlSmall = notice.value.imageUrlSmall?.substringBefore("?")
                val newNotice = notice.value.copy(imageUrl = prevUrl, imageUrlSmall = prevUrlSmall)
                _notice.emit(newNotice)
            }

            val request = RegisterNoticeRequest(
                content = content,
                imageUrl = notice.value.imageUrl,
                imageUrlSmall = notice.value.imageUrlSmall
            )

            runCatching { infoRepository.updateNotice(request, notice.value.articleId) }
                .onSuccess { goBackStack() }
                .onFailure { it.printStackTrace() }
        }
}
