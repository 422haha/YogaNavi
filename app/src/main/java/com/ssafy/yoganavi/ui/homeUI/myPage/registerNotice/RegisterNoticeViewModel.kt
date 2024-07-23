package com.ssafy.yoganavi.ui.homeUI.myPage.registerNotice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.source.notice.NoticeData
import com.ssafy.yoganavi.data.source.notice.RegisterNoticeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterNoticeViewModel @Inject constructor(
    private val infoRepository: InfoRepository
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
        _notice.emit(notice.value.copy(imageUrl = ""))
    }

    fun addImage(url: String) = viewModelScope.launch(Dispatchers.IO) {
        _notice.emit(notice.value.copy(imageUrl = url))
    }

    fun setContent(content:String)=viewModelScope.launch(Dispatchers.IO) {
        _notice.emit(notice.value.copy(content = content))
    }

    fun insertNotice(content: String, onSuccess: suspend () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val request = RegisterNoticeRequest(content = content, imageUrl = notice.value.imageUrl)
            runCatching { infoRepository.insertNotice(request) }
                .onSuccess { onSuccess() }
                .onFailure { it.printStackTrace() }
        }

    fun updateNotice(content: String, onSuccess: suspend () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val request = RegisterNoticeRequest(content = content, imageUrl = notice.value.imageUrl)
            runCatching { infoRepository.updateNotice(request, notice.value.articleId) }
                .onSuccess { onSuccess() }
                .onFailure { it.printStackTrace() }
        }
}
