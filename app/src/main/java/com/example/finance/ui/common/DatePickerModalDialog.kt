package com.example.finance.ui.common

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalDialog(
    onConfirmButtonClick: (Long) -> Unit,
    onDismiss: () -> Unit,
    initialSelectedDate: Long,
    selectableDates: SelectableDates,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDate,
        selectableDates = selectableDates
    )

    DatePickerDialog(
        confirmButton = {
            TextButton(
                onClick = { onConfirmButtonClick(datePickerState.selectedDateMillis!!) },
                enabled = datePickerState.selectedDateMillis != null
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
        DatePicker(state = datePickerState)
    }
}