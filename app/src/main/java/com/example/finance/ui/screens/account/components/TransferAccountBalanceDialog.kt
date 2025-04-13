package com.example.finance.ui.screens.account.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.finance.ui.theme.FinanceTheme

@Composable
fun TransferAccountBalanceDialog(
    accountBalance: Long,
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        title = { Text(text = "Перенос остатка на другой счет") },
        text = { Text(text = "На счету осталось $accountBalance ₽. Перевести их на другой счет?") },
        confirmButton = {
            TextButton(onClick = onConfirmButtonClick) {
                Text(text = "Да")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissButtonClick) {
                Text(text = "Нет")
            }
        },
        onDismissRequest = onDismissRequest,
        modifier = modifier
    )
}

@Preview
@Composable
private fun TransferAccountBalanceDialogPreview() {
    FinanceTheme {
        TransferAccountBalanceDialog(
            accountBalance = 1000,
            onConfirmButtonClick = {},
            onDismissButtonClick = {},
            onDismissRequest = {}
        )
    }
}