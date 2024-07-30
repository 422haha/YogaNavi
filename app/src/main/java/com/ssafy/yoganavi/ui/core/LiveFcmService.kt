package com.ssafy.yoganavi.ui.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.repository.DataStoreRepository
import com.ssafy.yoganavi.data.repository.UserRepository
import com.ssafy.yoganavi.ui.utils.CHANNEL_DESCRIPTION
import com.ssafy.yoganavi.ui.utils.CHANNEL_ID
import com.ssafy.yoganavi.ui.utils.CHANNEL_NAME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@AndroidEntryPoint
class LiveFcmService: FirebaseMessagingService() {
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            dataStoreRepository.setFcmToken(token)
            userRepository.updateFcmToken(token)
        }
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        sendNotification(remoteMessage)
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val channelId = CHANNEL_ID
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val uniId: Int = UUID.randomUUID().hashCode()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = CHANNEL_NAME
            val channelDescription = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            remoteMessage.data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, uniId, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"]
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"]

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)

        notificationManager.notify(uniId, notificationBuilder.build())
    }
}