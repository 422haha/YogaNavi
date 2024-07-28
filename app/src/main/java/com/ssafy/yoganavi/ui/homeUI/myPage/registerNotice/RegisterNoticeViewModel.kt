package com.ssafy.yoganavi.ui.homeUI.myPage.registerNotice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.data.source.dto.notice.RegisterNoticeRequest
import com.ssafy.yoganavi.ui.utils.BUCKET_NAME
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
        _notice.emit(notice.value.copy(imageUrl = "", imageUrlPath = "", imageUrlKey = ""))
    }

    fun addImage(url: String) = viewModelScope.launch(Dispatchers.IO) {
        _notice.emit(notice.value.copy(imageUrl = "", imageUrlPath = url, imageUrlKey = ""))
    }

    fun setContent(content: String) = viewModelScope.launch(Dispatchers.IO) {
        _notice.emit(notice.value.copy(content = content))
    }

    fun insertNotice(content: String, onSuccess: suspend () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {

            val imageUrlKey = "$NOTICE/${UUID.randomUUID()}"
            val imageUrl = s3Client.getUrl(BUCKET_NAME, imageUrlKey)

            val noticeFile = File(notice.value.imageUrlPath)
            val metadata = ObjectMetadata().apply { contentType = "image/webp" }
            transferUtility.upload(BUCKET_NAME, imageUrlKey, noticeFile, metadata)

            val request = RegisterNoticeRequest(content = content, imageUrl = imageUrl.toString())

            runCatching { infoRepository.insertNotice(request) }
                .onSuccess { onSuccess() }
                .onFailure { it.printStackTrace() }
        }

    fun updateNotice(content: String, onSuccess: suspend () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val imageUrlKey = "$NOTICE/${UUID.randomUUID()}"
            val imageUrl = s3Client.getUrl(BUCKET_NAME, imageUrlKey)

            if (notice.value.imageUrlPath.isNotBlank()) {
                val noticeFile = File(notice.value.imageUrlPath)
                val metadata = ObjectMetadata().apply { contentType = "image/webp" }
                transferUtility.upload(BUCKET_NAME, imageUrlKey, noticeFile, metadata)
                _notice.emit(notice.value.copy(imageUrl = imageUrl.toString()))
            } else {
                val imageUrl2 = notice.value.imageUrl.substringBefore("?")
                _notice.emit(notice.value.copy(imageUrl = imageUrl2))
            }
            val request = RegisterNoticeRequest(content = content, imageUrl = notice.value.imageUrl)

            runCatching { infoRepository.updateNotice(request, notice.value.articleId) }
                .onSuccess { onSuccess() }
                .onFailure { it.printStackTrace() }
        }
}
