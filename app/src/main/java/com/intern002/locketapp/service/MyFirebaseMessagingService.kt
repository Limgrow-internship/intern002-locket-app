package com.intern002.locketapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.intern002.locketapp.R
import com.intern002.locketapp.data.repository.FcmRepository
import com.intern002.locketapp.ui.BubbleActivity
import com.intern002.locketapp.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmRepository: FcmRepository

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "locket_notifications"
        private const val CHANNEL_MESSAGES_ID = "locket_messages"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        val notification = remoteMessage.notification
        val data = remoteMessage.data
        val notificationType = data["type"]

        if (notificationType == "NEW_MESSAGE") {
            showBubbleNotification(notification?.title, notification?.body, data)
        } else {
            sendDefaultNotification(notification?.title, notification?.body, data)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                fcmRepository.registerToken(token)
            } catch (e: Exception) {
                Log.e(TAG, "Error sending new token to server", e)
            }
        }
    }

    private fun showBubbleNotification(title: String?, messageBody: String?, data: Map<String, String>) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            sendDefaultNotification(title, messageBody, data)
            return
        }

        val conversationId = data["entityId"] ?: return

        val user = Person.Builder()
            .setName(title ?: "New Message")
            .setIcon(IconCompat.createWithResource(this, R.drawable.ic_locket_notification))
            .build()

        val bubbleIntent = Intent(this, BubbleActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra("conversation_id", conversationId)
        }
        val bubblePendingIntent = PendingIntent.getActivity(this, conversationId.hashCode(), bubbleIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val bubbleData = NotificationCompat.BubbleMetadata.Builder(bubblePendingIntent, IconCompat.createWithResource(this, R.drawable.ic_locket_notification))
            .setDesiredHeight(600)
            .build()

        val contentIntent = Intent(this, MainActivity::class.java).apply {
             putExtra("conversation_id", conversationId)
        }
        val contentPendingIntent = PendingIntent.getActivity(this, conversationId.hashCode(), contentIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_MESSAGES_ID)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.ic_locket_notification)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .addPerson(user)
            .setStyle(NotificationCompat.MessagingStyle(user).addMessage(messageBody ?: "", System.currentTimeMillis(), user))
            .setBubbleMetadata(bubbleData)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .setShortcutId(conversationId)
            .setShowWhen(true) // Hiển thị thời gian
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_MESSAGES_ID, "Messages", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(conversationId.hashCode(), notification)
    }

    private fun sendDefaultNotification(title: String?, messageBody: String?, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            data.forEach { (key, value) -> putExtra(key, value) }
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        // Tạo RemoteViews từ layout tùy chỉnh
        val notificationLayout = RemoteViews(packageName, R.layout.notification_custom)
        notificationLayout.setTextViewText(R.id.notification_title, title)
        notificationLayout.setTextViewText(R.id.notification_text, messageBody)
        notificationLayout.setImageViewResource(R.id.notification_icon, R.drawable.ic_locket_notification)
        notificationLayout.setTextViewText(R.id.notification_time, "Now") // Set thời gian


        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_locket_notification) // Icon này vẫn bắt buộc
            .setStyle(NotificationCompat.DecoratedCustomViewStyle()) // Cho phép layout tùy chỉnh
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout) // Dùng cho cả dạng mở rộng
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setWhen(System.currentTimeMillis()) 
            .setShowWhen(true) 

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Locket App Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }
}
