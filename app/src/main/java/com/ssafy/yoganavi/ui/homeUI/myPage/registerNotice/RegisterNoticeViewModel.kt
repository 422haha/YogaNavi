package com.ssafy.yoganavi.ui.homeUI.myPage.registerNotice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.InfoRepositoryImpl
import com.ssafy.yoganavi.data.source.notice.NoticeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterNoticeViewModel @Inject constructor(
    private val repositoryImpl: InfoRepositoryImpl
) : ViewModel() {
    private val _notice = MutableStateFlow(NoticeData())
    val notice: StateFlow<NoticeData> = _notice.asStateFlow()

    fun getNotice(articleId: Int) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { repositoryImpl.getNotice(articleId) }
            .onSuccess {
                it.data?.let { notice -> _notice.emit(notice) }
            }
            .onFailure { it.printStackTrace() }
    }

    fun removeImage() = viewModelScope.launch(Dispatchers.IO) {
        _notice.emit(notice.value.copy(imageUrl = ""))
    }

    fun addImage(url: String) = viewModelScope.launch(Dispatchers.IO) {
        _notice.emit(notice.value.copy(imageUrl = url))
    }
}
