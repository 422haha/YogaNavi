package com.ssafy.yoganavi.ui.homeUI.myPage.registerLive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.source.live.LiveLectureData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterLiveViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {

    private val _liveState = MutableStateFlow(LiveLectureData())
    val liveState: StateFlow<LiveLectureData> = _liveState.asStateFlow()

    fun getLive(liveId: Int) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getLive(liveId) }
            .onSuccess { it.data?.let { lecture -> _liveState.emit(lecture) } }
            .onFailure { it.printStackTrace() }
    }

    fun createLive() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.createLive() }
            .onSuccess {  }
            .onFailure { it.printStackTrace() }
    }
}
