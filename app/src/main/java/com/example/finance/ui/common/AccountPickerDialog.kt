package com.example.finance.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finance.domain.entities.Account

@Composable
fun AccountPickerDialog(
    accounts: List<Account>,
    initialSelectedAccountId: Int,
    onConfirmButtonClick: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAccountId by remember { mutableIntStateOf(initialSelectedAccountId) }

    AlertDialog(
        title = { Text("Выберите счет") },
        confirmButton = {
            TextButton(
                onClick = { onConfirmButtonClick(selectedAccountId) },
                enabled = selectedAccountId != -1
            ) {
                Text(text = "Выбрать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Отмена")
            }
        },
        text = {
            LazyColumn {
                if (accounts.isNotEmpty()) {
                    items(
                        items = accounts,
                        key = { it.id }
                    ) { account ->
                        AccountRadioButton(
                            account = account,
                            selected = account.id == selectedAccountId,
                            onClick = { selectedAccountId = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    item {
                        Text(text = "Нет доступных счетов")
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier.sizeIn(maxHeight = 400.dp)
    )
}

@Composable
private fun AccountRadioButton(
    account: Account,
    selected: Boolean,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.clickable { onClick(account.id) }
    ) {
        RadioButton(
            selected = selected,
            onClick = { onClick(account.id) }
        )

        Text(text = account.name)
    }
}