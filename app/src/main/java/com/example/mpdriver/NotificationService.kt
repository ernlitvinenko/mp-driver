package com.example.mpdriver

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import kotlin.random.Random


class NotificationService(private val context: Context) {
    val manager = context.getSystemService(NotificationManager::class.java)

    fun showNotificationAuthCode(code: String) {
        val notification = NotificationCompat.Builder(context, "mpdriver_notifications")
            .setContentTitle("MPDriver - код подтверждения")
            .setContentText("Ваш код подтверждения: $code")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setSmallIcon(R.drawable.tick_default)
            .setAutoCancel(true)
            .build()

        manager.notify(Random.nextInt(), notification)
    }
}