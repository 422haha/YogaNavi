package com.ssafy.yoganavi.ui.homeUI.schedule.live

import androidx.lifecycle.ViewModel
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.sessions.WebRtcSessionManager
import com.ssafy.yoganavi.ui.utils.CallMediaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LiveViewModel @Inject constructor(
    val sessionManager: WebRtcSessionManager,
    val infoRepository: InfoRepository
) : ViewModel() {
    private val _callMediaState = MutableStateFlow(CallMediaState())
    val callMediaState: StateFlow<CallMediaState> = _callMediaState

    private val _offsetX = MutableStateFlow(0f)
    val offsetX: StateFlow<Float> get() = _offsetX

    private val _offsetY = MutableStateFlow(0f)
    val offsetY: StateFlow<Float> get() = _offsetY

    fun updateOffset(x: Float, y: Float) {
        _offsetX.value = x
        _offsetY.value = y
    }

    fun toggleMicrophoneState(isEnabled: Boolean) {
        _callMediaState.value = _callMediaState.value.copy(isMicrophoneEnabled = isEnabled)
    }

    fun toggleCameraState(isEnabled: Boolean) {
        _callMediaState.value = _callMediaState.value.copy(isCameraEnabled = isEnabled)
    }
}
