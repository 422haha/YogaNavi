package com.ssafy.yoganavi.ui.homeUI.myPage.registerLive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.source.live.LiveLectureData
import com.ssafy.yoganavi.data.source.live.RegisterLiveRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegisterLiveViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {

    private val _liveState = RegisterLiveRequest()
    val liveState: RegisterLiveRequest = _liveState

    fun getLive(liveId: Int, onSuccess: (LiveLectureData) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getLive(liveId) }
            .onSuccess { it.data?.let { data -> onSuccess(data) } }
            .onFailure { it.printStackTrace() }
    }

    fun createLive() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.createLive(liveState) }
            .onFailure { it.printStackTrace() }
    }
}
