package com.example.finance.ui.screens.accountlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.ui.common.AddFloatingActionButton
import com.example.finance.ui.common.AppNavigationDrawer
import com.example.finance.ui.common.MenuTopBar
import com.example.finance.ui.screens.accountlist.components.AccountsList
import com.example.finance.ui.screens.accountlist.components.TotalAccountsSum
import com.example.finance.ui.screens.accountlist.components.TransferIcon
import kotlinx.coroutines.launch

@Composable
fun AccountListScreen(
    selectedNavigationItemIndex: Int,
    onFloatingButtonClick: () -> Unit,
    onNavigationItemClick: (Int) -> Unit,
    onTransferIconClick: () -> Unit,
    onAccountItemClick: (Int) -> Unit,
    viewModel: AccountListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppNavigationDrawer(
        selectedIndex = selectedNavigationItemIndex,
        drawerState = drawerState,
        onNavigationItemClick = onNavigationItemClick
    ) {
        Scaffold(
            topBar = {
                MenuTopBar(
                    title = { Text(text = "Счета") },
                    onMenuIconClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            floatingActionButton = { AddFloatingActionButton(onClick = onFloatingButtonClick) },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = paddingValues.calculateTopPadding() + 12.dp)
                    .padding(horizontal = 16.dp)
            ) {
                TotalAccountsSum(totalSum = uiState.totalSum)

                TransferIcon(onClick = onTransferIconClick)

                AccountsList(
                    accounts = uiState.accounts,
                    onAccountClick = onAccountItemClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}