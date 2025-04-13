package com.example.finance.data.scheduler

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.finance.domain.entities.Periodicity
import com.example.finance.domain.entities.Reminder
import com.example.finance.domain.scheduler.ReminderScheduler
import com.example.finance.ui.ReminderWorker
import com.example.finance.utils.toMillis
import java.util.UUID
import java.util.concurrent.TimeUnit

class ReminderSchedulerImpl(
    private val workManager: WorkManager
) : ReminderScheduler {

    override fun scheduleReminder(reminder: Reminder): UUID {
        val inputData = Data.Builder().apply {
            putInt(ReminderWorker.REMINDER_ID_KEY, reminder.id)
            putString(ReminderWorker.NOTIFICATION_TITLE_KEY, reminder.name)
            putString(ReminderWorker.NOTIFICATION_TEXT_KEY, reminder.comment)
        }.build()

        val initialDelay = reminder.date.toMillis() - System.currentTimeMillis()

        val workRequest: WorkRequest = when (reminder.periodicity) {
            Periodicity.ONCE -> {
                OneTimeWorkRequestBuilder<ReminderWorker>().apply {
                    setInputData(inputData)
                    setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                }.build()
            }

            Periodicity.DAY -> {
                PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS).apply {
                    setInputData(inputData)
                    setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                }.build()
            }

            Periodicity.WEEK -> {
                PeriodicWorkRequestBuilder<ReminderWorker>(7, TimeUnit.DAYS).apply {
                    setInputData(inputData)
                    setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                }.build()
            }

            Periodicity.MONTH -> {
                PeriodicWorkRequestBuilder<ReminderWorker>(31, TimeUnit.DAYS).apply {
                    setInputData(inputData)
                    setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                }.build()
            }

            Periodicity.YEAR -> {
                PeriodicWorkRequestBuilder<ReminderWorker>(365, TimeUnit.DAYS).apply {
                    setInputData(inputData)
                    setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                }.build()
            }
        }

        workManager.enqueue(workRequest)
        return workRequest.id
    }

    override fun cancelReminder(reminder: Reminder) {
        reminder.workId?.let { workManager.cancelWorkById(reminder.workId) }
    }
}