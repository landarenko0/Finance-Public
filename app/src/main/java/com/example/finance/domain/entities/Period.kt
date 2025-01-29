package com.example.finance.domain.entities

import java.time.LocalDate

data class Period(
    val startDate: LocalDate,
    val endDate: LocalDate
)
