package com.example.finance.domain.notifications

interface NotificationService {

    fun createNotificationAndNotify(notificationId: Int, title: String, text: String): Boolean
}