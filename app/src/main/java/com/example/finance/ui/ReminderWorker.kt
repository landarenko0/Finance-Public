package com.example.finance.ui

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finance.domain.entities.Periodicity
import com.example.finance.domain.usecases.ReminderInteractor
import com.example.finance.ui.utils.NotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val notificationService: NotificationService,
    private val reminderInteractor: ReminderInteractor
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val reminderId = workerParams.inputData.getInt(REMINDER_ID_KEY, 0)
        val notificationTitle = workerParams.inputData.getString(NOTIFICATION_TITLE_KEY)
        val notificationText = workerParams.inputData.getString(NOTIFICATION_TEXT_KEY)

        if (notificationTitle != null && notificationText != null) {
            val notificationCreated = notificationService.createNotificationAndNotify(
                notificationId = reminderId,
                title = notificationTitle,
                text = notificationText
            )

            val reminder = reminderInteractor.getReminderById(reminderId)

            val nextNotificationDate = when (reminder.periodicity) {
                Periodicity.ONCE -> {
                    reminderInteractor.updateReminder(
                        reminder.copy(
                            isActive = false,
                            workId = null
                        )
                    )
                    return@withContext Result.success()
                }

                Periodicity.DAY -> reminder.date.plusDays(1)
                Periodicity.WEEK -> reminder.date.plusDays(7)
                Periodicity.MONTH -> reminder.date.plusDays(31)
                Periodicity.YEAR -> reminder.date.plusDays(365)
            }

            reminderInteractor.updateReminder(reminder.copy(date = nextNotificationDate))

            return@withContext if (notificationCreated) Result.success() else Result.failure()
        }

        return@withContext Result.failure()
    }

    companion object {
        const val REMINDER_ID_KEY = "notification_id_key"
        const val NOTIFICATION_TITLE_KEY = "notification_title_key"
        const val NOTIFICATION_TEXT_KEY = "notification_text_key"
    }
}