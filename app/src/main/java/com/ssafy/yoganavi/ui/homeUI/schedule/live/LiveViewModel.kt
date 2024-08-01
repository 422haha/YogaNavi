package com.ssafy.yoganavi.ui.homeUI.schedule.live

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.sessions.WebRtcSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import org.webrtc.VideoTrack
import javax.inject.Inject

@HiltViewModel
class LiveViewModel @Inject constructor(
    val sessionManager: WebRtcSessionManager)
    : ViewModel() {

    private val _videoTrack = MutableLiveData<VideoTrack?>()
    val videoTrack: LiveData<VideoTrack?> = _videoTrack

    fun setVideoTrack(track: VideoTrack?) {
        _videoTrack.value = track
    }

    override fun onCleared() {
        super.onCleared()

        _videoTrack.value = null
    }
}
