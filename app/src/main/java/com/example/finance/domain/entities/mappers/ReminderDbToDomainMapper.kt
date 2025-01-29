package com.example.finance.domain.entities.mappers

import com.example.finance.data.local.entities.ReminderDb
import com.example.finance.domain.entities.Reminder

class ReminderDbToDomainMapper : (ReminderDb) -> Reminder {

    override fun invoke(reminderDb: ReminderDb): Reminder = Reminder(
        id = reminderDb.id,
        name = reminderDb.name,
        periodicity = reminderDb.periodicity,
        date = reminderDb.date,
        comment = reminderDb.comment,
        isActive = reminderDb.isActive
    )
}