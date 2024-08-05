package com.ssafy.yoganavi.di

import android.content.Context
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.SignalingClient
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.peer.StreamPeerConnectionFactory
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.sessions.WebRtcSessionManager
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.sessions.WebRtcSessionManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object WebRtcModule {
    @Provides
    @ViewModelScoped
    fun provideSignalingClient(): SignalingClient {
        return SignalingClient()
    }

    @Provides
    @ViewModelScoped
    fun providePeerConnectionFactory(@ApplicationContext context: Context): StreamPeerConnectionFactory {
        return StreamPeerConnectionFactory(context)
    }

    @Provides
    @ViewModelScoped
    fun provideWebRtcSessionManager(
        @ApplicationContext context: Context,
        signalingClient: SignalingClient,
        peerConnectionFactory: StreamPeerConnectionFactory
    ): WebRtcSessionManager {
        return WebRtcSessionManagerImpl(context, signalingClient, peerConnectionFactory)
    }
}