package com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.sessions

import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.SignalingClient
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.peer.StreamPeerConnectionFactory
import kotlinx.coroutines.flow.SharedFlow
import org.webrtc.VideoTrack

interface WebRtcSessionManager {

  val signalingClient: SignalingClient

  val peerConnectionFactory: StreamPeerConnectionFactory

  val localVideoTrackFlow: SharedFlow<VideoTrack>

  val remoteVideoTrackFlow: SharedFlow<VideoTrack>

  fun onSessionScreenReady()

  fun flipCamera()

  fun enableMicrophone(enabled: Boolean)

  fun enableCamera(enabled: Boolean)

  fun disconnect()
}
