package com.example.finance.ui.screens.account

import com.example.finance.domain.entities.Account

data class AccountUiState(
    val accountSum: String = "",
    val accountName: String = "",
    val accountSumError: Boolean = false,
    val accountNameError: Boolean = false,
    val requestAccountSumFocus: Boolean = false,
    val requestAccountNameFocus: Boolean = false,
    val showAccountNameCollisionDialog: Boolean = false,
    val accounts: List<Account> = emptyList(),
    val closeScreen: Boolean = false,
    val details: AccountDetails = AccountDetails.Initial
)

sealed interface AccountDetails {

    data object Initial : AccountDetails

    data object CreateAccount : AccountDetails

    data class EditAccount(
        val showDeleteAccountDialog: Boolean = false,
        val showTransferAccountBalanceDialog: Boolean = false,
        val showSelectAccountDialog: Boolean = false,
        val accountSum: Long = 0
    ) : AccountDetails
}