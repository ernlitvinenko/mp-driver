package com.example.mpdriver

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import kotlin.random.Random


data class NotificationData(
    val title: String,
    val text: String
)

class NotificationService(private val context: Context) {
    val manager = context.getSystemService(NotificationManager::class.java)
    private val notificationChannel = "mpdriver_notifications"

    fun showNotificationAuthCode(code: String) {
        showNotification(
            NotificationData(
                "MPDriver - код подтверждения",
                "Ваш код подтверждения: $code"
            )
        )
    }

    fun showNotification(data: NotificationData) {
        val notification =
            NotificationCompat.Builder(context, notificationChannel).setContentTitle(data.title)
                .setContentText(data.text)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setSmallIcon(R.drawable.hottransport)
                .build()

        manager.notify(Random.nextInt(), notification)
    }
}