package com.example.finance.ui.screens.accountlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finance.domain.entities.Account
import com.example.finance.ui.theme.FinanceTheme

@Composable
fun AccountsList(
    accounts: List<Account>,
    onAccountClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = modifier
    ) {
        items(
            items = accounts,
            key = { it.id }
        ) { account ->
            AccountItem(
                account = account,
                onClick = onAccountClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AccountItem(
    account: Account,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick(account.id) },
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = account.name,
                style = MaterialTheme.typography.displayMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${account.sum} ₽",
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountItemPreview() {
    FinanceTheme {
        AccountItem(
            account = Account(id = 0, name = "Основной", sum = 100000),
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}