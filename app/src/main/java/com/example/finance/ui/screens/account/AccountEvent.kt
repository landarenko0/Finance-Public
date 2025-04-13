package com.example.finance.ui.screens.account

sealed interface AccountEvent {

    data object CloseScreen : AccountEvent

    data object RequestAccountBalanceFocus : AccountEvent

    data object RequestAccountNameFocus : AccountEvent
}