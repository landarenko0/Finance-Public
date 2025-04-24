package com.example.finance.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finance.domain.entities.Account
import com.example.finance.ui.theme.FinanceTheme

@Composable
fun AccountPickerWithBalanceTopBarTitle(
    selectedAccount: Account,
    onPickerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        Picker(
            text = selectedAccount.name,
            onClick = onPickerClick
        )

        Text(text = "${selectedAccount.balance} ₽")
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenTopBarTitlePreview() {
    FinanceTheme {
        AccountPickerWithBalanceTopBarTitle(
            selectedAccount = Account(id = 0, name = "Основной", balance = 100000),
            onPickerClick = {}
        )
    }
}