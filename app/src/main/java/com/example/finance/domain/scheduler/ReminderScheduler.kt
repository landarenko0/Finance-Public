package com.example.finance.domain.scheduler

import com.example.finance.domain.entities.Reminder
import java.util.UUID

interface ReminderScheduler {

    fun scheduleReminder(reminder: Reminder): UUID

    fun cancelReminder(reminder: Reminder)
}