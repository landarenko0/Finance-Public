package com.example.finance.ui.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
object PastOrPresentSelectableDates : SelectableDates {

    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        utcTimeMillis <= System.currentTimeMillis()

    override fun isSelectableYear(year: Int): Boolean = year <= LocalDate.now().year
}