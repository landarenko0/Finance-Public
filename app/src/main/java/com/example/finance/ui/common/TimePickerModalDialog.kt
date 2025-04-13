package com.example.finance.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModalDialog(
    onConfirmButtonClick: (Pair<Int, Int>) -> Unit,
    onDismiss: () -> Unit,
    initialSelectedTime: Pair<Int, Int>,
    modifier: Modifier = Modifier
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialSelectedTime.first,
        initialMinute = initialSelectedTime.second,
        is24Hour = true
    )

    TimePickerDialog(
        state = timePickerState,
        onConfirmButtonClick = {
            onConfirmButtonClick(timePickerState.hour to timePickerState.minute)
        },
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    state: TimePickerState,
    onConfirmButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        title = { Text(text = "Выберите время напоминания") },
        confirmButton = {
            TextButton(onClick = onConfirmButtonClick) {
                Text(text = "Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Отмена")
            }
        },
        text = {
            TimePicker(state = state)
        },
        onDismissRequest = onDismiss,
        modifier = modifier
    )
}