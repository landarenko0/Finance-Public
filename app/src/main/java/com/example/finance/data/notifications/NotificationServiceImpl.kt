package com.example.finance.data.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.finance.R
import com.example.finance.domain.notifications.NotificationService
import com.example.finance.ui.MainActivity

class NotificationServiceImpl(
    private val context: Context
): NotificationService {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    override fun createNotificationAndNotify(
        notificationId: Int,
        title: String,
        text: String
    ): Boolean {
        createNotificationChannel()

        val canSendNotifications = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (canSendNotifications) {
            NotificationCompat.Builder(context, APP_CHANNEL_ID).apply {
                setSmallIcon(R.mipmap.ic_launcher)
                setContentTitle(title)
                setAutoCancel(true)
                setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        notificationId,
                        Intent(context, MainActivity::class.java),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
                setStyle(NotificationCompat.BigTextStyle())
                if (text.isNotEmpty()) setContentText(text)
            }.build().also { notificationManager.notify(notificationId, it) }

            return true
        }

        return false
    }

    private fun createNotificationChannel() {
        NotificationChannel(
            APP_CHANNEL_ID,
            APP_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).also { notificationManager?.createNotificationChannel(it) }
    }

    companion object {
        private const val APP_CHANNEL_ID = "reminder"
        private const val APP_CHANNEL_NAME = "Напоминания"
    }
}