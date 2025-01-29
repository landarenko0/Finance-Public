package com.example.finance.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ConfirmationDialog(
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        title = { Text(text = "Подтверждение действия") },
        text = { Text(text = text) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Да")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Нет")
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier
    )
}