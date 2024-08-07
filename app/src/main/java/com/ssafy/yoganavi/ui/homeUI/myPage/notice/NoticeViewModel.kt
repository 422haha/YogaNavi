package com.ssafy.yoganavi.ui.homeUI.myPage.notice

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.s3.AmazonS3Client
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.ui.utils.loadS3Image
import com.ssafy.yoganavi.ui.utils.loadS3ImageSequentially
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val s3Client: AmazonS3Client
) : ViewModel() {
    private val _noticeList = MutableStateFlow<List<NoticeData>>(emptyList())
    val noticeList = _noticeList.asStateFlow()

    fun getNoticeAll() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getNoticeList() }
            .onSuccess { _noticeList.emit(it.data.toMutableList()) }
            .onFailure { it.printStackTrace() }
    }

    fun deleteNotice(articleId: Int) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.deleteNotice(articleId) }
            .onSuccess { getNoticeAll() }
            .onFailure { it.printStackTrace() }
    }

    fun loadS3Image(view: ImageView, key: String) = view.loadS3Image(key, s3Client)

    fun loadS3ImageSequentially(
        view: ImageView,
        smallKey: String,
        largeKey: String
    ) = view.loadS3ImageSequentially(smallKey, largeKey, s3Client)
}
