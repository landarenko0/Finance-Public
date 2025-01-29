package com.example.finance.domain.entities

import java.time.LocalDateTime

data class Reminder(
    val id: Int,
    val name: String,
    val periodicity: Periodicity,
    val date: LocalDateTime,
    val comment: String,
    val isActive: Boolean
)
