package com.ssafy.yoganavi.ui.homeUI.schedule.live

import androidx.lifecycle.ViewModel
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.sessions.WebRtcSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LiveViewModel @Inject constructor(val sessionManager: WebRtcSessionManager) : ViewModel() {
}
