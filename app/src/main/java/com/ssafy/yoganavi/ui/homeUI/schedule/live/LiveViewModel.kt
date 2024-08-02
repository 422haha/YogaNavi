package com.ssafy.yoganavi.ui.homeUI.schedule.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.WebRTCSessionState
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.sessions.WebRtcSessionManager
import com.ssafy.yoganavi.ui.utils.CallMediaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveViewModel @Inject constructor(val sessionManager: WebRtcSessionManager) : ViewModel() {
    private val _callMediaState = MutableStateFlow(CallMediaState())
    val callMediaState: StateFlow<CallMediaState> = _callMediaState

    private val _sessionState = MutableStateFlow(WebRTCSessionState.Offline)
    val sessionState: StateFlow<WebRTCSessionState> = _sessionState

    init {
        viewModelScope.launch {
            sessionManager.signalingClient.sessionStateFlow.collect { state ->
                _sessionState.value = state
                if (state == WebRTCSessionState.Ready) {
                    sessionManager.onSessionScreenReady()
                }
            }
        }
    }

    fun toggleMicrophoneState(isEnabled: Boolean) {
        _callMediaState.value = _callMediaState.value.copy(isMicrophoneEnabled = isEnabled)
    }

    fun toggleCameraState(isEnabled: Boolean) {
        _callMediaState.value = _callMediaState.value.copy(isCameraEnabled = isEnabled)
    }
}
