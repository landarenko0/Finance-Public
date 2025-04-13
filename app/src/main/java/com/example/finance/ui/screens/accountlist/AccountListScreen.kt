package com.example.finance.ui.screens.accountlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.ui.common.AddFloatingActionButton
import com.example.finance.ui.common.MenuTopBar
import com.example.finance.ui.screens.accountlist.components.AccountsList
import com.example.finance.ui.screens.accountlist.components.TotalAccountsBalance
import com.example.finance.ui.screens.accountlist.components.TransferIcon

@Composable
fun AccountListScreen(
    navigateToCreateAccountScreen: () -> Unit,
    navigateToCreateTransferScreen: () -> Unit,
    navigateToEditAccountScreen: (Int) -> Unit,
    openNavigationDrawer: () -> Unit,
    viewModel: AccountListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MenuTopBar(
                title = { Text(text = "Счета") },
                onMenuIconClick = openNavigationDrawer,
                modifier = Modifier.fillMaxWidth()
            )
        },
        floatingActionButton = { AddFloatingActionButton(onClick = navigateToCreateAccountScreen) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            TotalAccountsBalance(totalSum = uiState.totalBalance)

            TransferIcon(onClick = navigateToCreateTransferScreen)

            if (uiState.accounts.isNotEmpty()) {
                AccountsList(
                    accounts = uiState.accounts,
                    onAccountClick = navigateToEditAccountScreen,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "Нет счетов")
                }
            }
        }
    }
}