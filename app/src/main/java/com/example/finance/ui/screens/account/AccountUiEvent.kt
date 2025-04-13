package com.example.finance.ui.screens.account

sealed interface AccountUiEvent {

    data class OnAccountBalanceChanged(val accountBalance: String) : AccountUiEvent

    data class OnAccountNameChanged(val accountName: String) : AccountUiEvent

    data object OnDeleteIconClick : AccountUiEvent

    data object OnBackIconClick : AccountUiEvent

    data object OnConfirmAccountNameCollisionDialog : AccountUiEvent

    data object OnConfirmDeleteAccountDialog : AccountUiEvent

    data object OnConfirmTransferAccountBalanceDialog : AccountUiEvent

    data object OnDismissTransferAccountBalanceDialog : AccountUiEvent

    data class OnTransferAccountSelected(val accountId: Int) : AccountUiEvent

    data object OnDismissDialog : AccountUiEvent

    data object OnSaveButtonClick : AccountUiEvent
}