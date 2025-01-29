package com.example.finance.ui.screens.main.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finance.ui.common.PastOrPresentSelectableDates

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModalDialog(
    onConfirm: (Pair<Long, Long>) -> Unit,
    onDismiss: () -> Unit,
    initialSelectedStartDate: Long?,
    initialSelectedEndDate: Long?,
    modifier: Modifier = Modifier
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialSelectedStartDate,
        initialSelectedEndDateMillis = initialSelectedEndDate,
        selectableDates = PastOrPresentSelectableDates
    )

    DatePickerDialog(
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis!!,
                            dateRangePickerState.selectedEndDateMillis!!
                        )
                    )
                },
                enabled = dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null
            ) {
                Text(text = "Выбрать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Отмена")
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(top = 16.dp)
        )
    }
}