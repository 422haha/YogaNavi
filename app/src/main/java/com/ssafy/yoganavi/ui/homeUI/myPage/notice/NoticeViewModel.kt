package com.ssafy.yoganavi.ui.homeUI.myPage.notice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.repository.ListResponse
import com.ssafy.yoganavi.data.source.notice.NoticeData
import com.ssafy.yoganavi.ui.utils.NO_RESPONSE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val infoRepository : InfoRepository
) : ViewModel() {
    private val _noticeList = MutableStateFlow<List<NoticeData>>(emptyList())
    val noticeList = _noticeList.asStateFlow()

    fun getNoticeAll() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getNoticeList() }
            .onSuccess { _noticeList.emit(it.data)
                Log.d("ssafy", "getNoticeAll: ${it.data}")}
            .onFailure { it.printStackTrace()
                Log.d("싸피", "getNoticeAll: 실패")
        }
    }
}
