package com.ssafy.yoganavi.ui.homeUI.myPage.notice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.source.notice.NoticeData
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
}
