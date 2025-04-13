package com.example.finance.domain.entities

import java.time.LocalDateTime
import java.util.UUID

data class Reminder(
    val id: Int,
    val name: String,
    val periodicity: Periodicity,
    val date: LocalDateTime,
    val comment: String,
    val isActive: Boolean,
    val workId: UUID?
)
