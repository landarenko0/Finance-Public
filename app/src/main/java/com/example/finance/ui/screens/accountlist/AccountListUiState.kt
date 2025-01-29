package com.example.finance.ui.screens.accountlist

import com.example.finance.domain.entities.Account

data class AccountListUiState(
    val accounts: List<Account> = emptyList(),
    val totalSum: Long = 0
)
