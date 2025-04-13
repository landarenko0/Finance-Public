package com.example.finance.ui.utils

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
import com.example.finance.ui.MainActivity

class NotificationService(private val context: Context) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun createNotificationAndNotify(notificationId: Int, title: String, text: String): Boolean {
        createNotificationChannel()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
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
        private const val APP_CHANNEL_ID = "finance_app"
        private const val APP_CHANNEL_NAME = "Напоминания"
    }
}