package com.site.pochak.app

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

import android.app.NotificationManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.site.pochak.app.core.datastore.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MyFirebaseMsgService"

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var tokenManager: TokenManager

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "MyFirebaseMessagingService created")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token: $token")
        serviceScope.launch {
            tokenManager.setFcmTokenSent(false)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "data: ${remoteMessage.data}")
        Log.d(TAG, "notification: ${remoteMessage.notification}")

        sendNotification(remoteMessage)

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "ChannelName"
            val channelDescription = "ChannelDescription"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val notificationChannel = NotificationChannel(
                CHANNEL_ID, channelName, importance
            ).apply {
                description = channelDescription
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

            Log.d(TAG, "Notification Channel Created: $channelName")
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "Default Title"
        val message = remoteMessage.notification?.body ?: "Default Message"
        val imageUrl = remoteMessage.notification?.imageUrl

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "ChannelName"
            val channelDescription = "ChannelDescription"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, channelName, importance
            ).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = imageUrl?.let { getBitmapFromCoil(it.toString()) }
            val notificationBuilder =
                NotificationCompat.Builder(this@MyFirebaseMessagingService, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_fcm_logo)
                    .setColor(resources.getColor(R.color.yellow, null))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)

            // 확장 전 알림에 표시될 largeIcon 설정
            bitmap?.let {
                notificationBuilder.setLargeIcon(it) // 대형 아이콘 설정
                val style = NotificationCompat.BigPictureStyle()
                    .bigPicture(it)
                    .bigLargeIcon(null as Bitmap?) // 확장된 상태에서는 largeIcon 숨김
                notificationBuilder.setStyle(style)
            }

            notificationManager.notify(
                System.currentTimeMillis().toInt(),
                notificationBuilder.build()
            )
        }
    }

    private suspend fun getBitmapFromCoil(imageUrl: String): Bitmap? {
        return try {
            val loader = ImageLoader(this)
            val request = ImageRequest.Builder(this)
                .data(imageUrl)
                .allowHardware(false)
                .build()
            val result = (loader.execute(request) as? SuccessResult)?.drawable
            (result as? BitmapDrawable)?.bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading image with Coil", e)
            null
        }
    }




    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "ChannelID"
    }
}
