package com.example.finance.data.local.entities.mappers

import com.example.finance.data.local.entities.ReminderDb
import com.example.finance.domain.entities.Reminder

fun Reminder.toDb(): ReminderDb = ReminderDb(
    id = this.id,
    name = this.name,
    periodicity = this.periodicity,
    date = this.date,
    comment = this.comment,
    isActive = this.isActive,
    workId = this.workId?.toString()
)

fun List<Reminder>.toDb(): List<ReminderDb> = this.map { it.toDb() }