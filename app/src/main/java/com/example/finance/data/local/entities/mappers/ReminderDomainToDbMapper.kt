package com.example.finance.data.local.entities.mappers

import com.example.finance.data.local.entities.ReminderDb
import com.example.finance.domain.entities.Reminder

class ReminderDomainToDbMapper : (Reminder) -> ReminderDb {

    override fun invoke(reminder: Reminder): ReminderDb = ReminderDb(
        id = reminder.id,
        name = reminder.name,
        periodicity = reminder.periodicity,
        date = reminder.date,
        comment = reminder.comment,
        isActive = reminder.isActive
    )
}