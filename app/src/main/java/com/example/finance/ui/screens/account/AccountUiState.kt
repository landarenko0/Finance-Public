package com.example.finance.ui.screens.account

import com.example.finance.domain.entities.Account

data class AccountUiState(
    val accountBalance: String = "",
    val accountName: String = "",
    val accountBalanceError: Boolean = false,
    val accountNameError: Boolean = false,
    val showAccountNameCollisionDialog: Boolean = false,
    val details: AccountDetails = AccountDetails.Initial
)

sealed interface AccountDetails {

    data object Initial : AccountDetails

    data object CreateAccount : AccountDetails

    data class EditAccount(
        val otherAccounts: List<Account> = emptyList(),
        val showDeleteAccountDialog: Boolean = false,
        val showTransferAccountBalanceDialog: Boolean = false,
        val showSelectAccountDialog: Boolean = false,
        val accountBalance: Long = 0
    ) : AccountDetails
}