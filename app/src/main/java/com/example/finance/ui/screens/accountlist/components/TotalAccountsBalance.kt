package com.example.finance.ui.screens.accountlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finance.ui.theme.FinanceTheme

@Composable
fun TotalAccountsBalance(
    totalSum: Long,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
    ) {
        Text(
            text = "Итого:",
            style = MaterialTheme.typography.displayMedium
        )

        Text(
            text = "$totalSum ₽",
            style = MaterialTheme.typography.displayLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TotalAccountsSumPreview() {
    FinanceTheme {
        TotalAccountsBalance(totalSum = 100000)
    }
}