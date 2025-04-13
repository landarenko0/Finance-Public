package com.example.finance.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun LocalDateText(
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM")
    val formattedDate = date.format(formatter)

    Text(
        text = formattedDate,
        modifier = modifier
    )
}