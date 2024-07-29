package com.ssafy.yoganavi.ui.core

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class LiveFcmService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
    
    // 백그라운드 data 처리
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }

    
}