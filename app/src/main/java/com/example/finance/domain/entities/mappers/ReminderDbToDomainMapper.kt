package com.example.finance.domain.entities.mappers

import com.example.finance.data.local.entities.ReminderDb
import com.example.finance.domain.entities.Reminder
import java.util.UUID

fun ReminderDb.toDomain(): Reminder = Reminder(
    id = this.id,
    name = this.name,
    periodicity = this.periodicity,
    date = this.date,
    comment = this.comment,
    isActive = this.isActive,
    workId = this.workId?.let { UUID.fromString(this.workId) }
)

fun List<ReminderDb>.toDomain(): List<Reminder> = this.map { it.toDomain() }