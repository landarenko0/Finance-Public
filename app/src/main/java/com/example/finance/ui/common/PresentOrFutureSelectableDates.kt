package com.example.finance.ui.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
object PresentOrFutureSelectableDates : SelectableDates {

    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate() >= LocalDate.now()

    override fun isSelectableYear(year: Int): Boolean = year >= LocalDate.now().year
}