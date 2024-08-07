package com.ssafy.yoganavi.ui.homeUI.myPage.registerNotice

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.data.source.dto.notice.RegisterNoticeRequest
import com.ssafy.yoganavi.ui.utils.MINI
import com.ssafy.yoganavi.ui.utils.NOTICE
import com.ssafy.yoganavi.ui.utils.loadS3ImageSequentially
import com.ssafy.yoganavi.ui.utils.uploadFile
import dagger.hilt.android.lifecycle.HiltViewModel
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
            imageKey = "",
            smallImageKey = "",
            imageUrlPath = "",
            imageUrlSmallPath = "",
        )
        _notice.emit(newNotice)
    }

    fun addImage(path: String, smallPath: String) = viewModelScope.launch(Dispatchers.IO) {
        val newNotice = notice.value.copy(
            imageUrlPath = path,
            imageKey = "",
            imageUrlSmallPath = smallPath,
            smallImageKey = ""
        )
        _notice.emit(newNotice)
    }

    fun setContent(content: String) = viewModelScope.launch(Dispatchers.IO) {
        _notice.emit(notice.value.copy(content = content))
    }

    fun insertNotice(
        content: String,
        showLoadingView: suspend () -> Unit,
        goBackStack: suspend () -> Unit,
        uploadFail: suspend () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        showLoadingView()

        val uuid = UUID.randomUUID()
        val imageUrlKey = "$NOTICE/$uuid"
        val imageUrlSmallKey = "$NOTICE/$MINI/$uuid"

        if (notice.value.imageUrlPath.isNotBlank()) {

            val noticeFile = File(notice.value.imageUrlPath)
            val miniFile = File(notice.value.imageUrlSmallPath)

            val metadata = ObjectMetadata().apply { contentType = "image/webp" }
            val uploadResults = awaitAll(
                async { uploadFile(transferUtility, imageUrlKey, noticeFile, metadata) },
                async { uploadFile(transferUtility, imageUrlSmallKey, miniFile, metadata) }
            )

            if (uploadResults.any { false }) {
                uploadFail()
                return@launch
            }
        }

        var request = RegisterNoticeRequest(
            content = content,
            imageUrl = imageUrlKey,
            imageUrlSmall = imageUrlSmallKey
        )

        if (notice.value.imageUrlPath.isBlank()) {
            request = RegisterNoticeRequest(content = content, imageUrl = "")
        }

        runCatching { infoRepository.insertNotice(request) }
            .onSuccess { goBackStack() }
            .onFailure { uploadFail() }
    }

    fun updateNotice(
        content: String,
        showLoadingView: suspend () -> Unit,
        goBackStack: suspend () -> Unit,
        uploadFail: suspend () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        showLoadingView()

        val uuid = UUID.randomUUID()
        val imageUrlKey = "$NOTICE/$uuid"
        val imageUrlSmallKey = "$NOTICE/$MINI/$uuid"

        if (notice.value.imageUrlPath.isNotBlank()) {
            val noticeFile = File(notice.value.imageUrlPath)
            val miniFile = File(notice.value.imageUrlSmallPath)
            val metadata = ObjectMetadata().apply { contentType = "image/webp" }

            val uploadResults = awaitAll(
                async { uploadFile(transferUtility, imageUrlKey, noticeFile, metadata) },
                async { uploadFile(transferUtility, imageUrlSmallKey, miniFile, metadata) }
            )

            if (uploadResults.any { false }) {
                uploadFail()
                return@launch
            }
        }

        val request = RegisterNoticeRequest(
            content = content,
            imageUrl = notice.value.imageKey,
            imageUrlSmall = notice.value.smallImageKey
        )

        runCatching { infoRepository.updateNotice(request, notice.value.articleId) }
            .onSuccess { goBackStack() }
            .onFailure { uploadFail() }
    }


    fun loadS3ImageSequentially(
        view: ImageView,
        smallKey: String,
        largeKey: String
    ) = view.loadS3ImageSequentially(smallKey, largeKey, s3Client)

}
