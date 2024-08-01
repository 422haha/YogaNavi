package com.ssafy.yoganavi.di

import android.content.Context
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.SignalingClient
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.peer.StreamPeerConnectionFactory
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.sessions.WebRtcSessionManager
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.sessions.WebRtcSessionManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WebRtcModule {
    @Provides
    @Singleton
    fun provideSignalingClient(): SignalingClient {
        return SignalingClient()
    }

    @Provides
    @Singleton
    fun providePeerConnectionFactory(@ApplicationContext context: Context): StreamPeerConnectionFactory {
        return StreamPeerConnectionFactory(context)
    }

    @Provides
    @Singleton
    fun provideWebRtcSessionManager(
        @ApplicationContext context: Context,
        signalingClient: SignalingClient,
        peerConnectionFactory: StreamPeerConnectionFactory
    ): WebRtcSessionManager {
        return WebRtcSessionManagerImpl(context, signalingClient, peerConnectionFactory)
    }
}