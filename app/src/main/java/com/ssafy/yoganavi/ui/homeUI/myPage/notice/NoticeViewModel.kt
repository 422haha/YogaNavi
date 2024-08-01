package com.ssafy.yoganavi.ui.homeUI.myPage.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.repository.response.AuthException
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {
    private val _noticeList = MutableStateFlow<List<NoticeData>>(emptyList())
    val noticeList = _noticeList.asStateFlow()

    fun getNoticeAll(endSession: suspend () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getNoticeList() }
            .onSuccess { _noticeList.emit(it.data.toMutableList()) }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }

    fun deleteNotice(
        articleId: Int,
        endSession: suspend () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.deleteNotice(articleId) }
            .onSuccess { getNoticeAll(endSession) }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }
}
