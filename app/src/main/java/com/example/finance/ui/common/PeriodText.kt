package com.example.finance.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.finance.domain.entities.Period
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PeriodText(
    period: Period,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM")

    val formattedStartDate = period.startDate.format(formatter)

    val periodText = when {
        period.startDate == period.endDate && LocalDate.now() == period.startDate -> "Сегодня, $formattedStartDate"

        period.startDate == period.endDate -> formattedStartDate

        else -> {
            val formattedEndDate = period.endDate.format(formatter)
            "$formattedStartDate - $formattedEndDate"
        }
    }

    Text(
        text = periodText,
        modifier = modifier
    )
}