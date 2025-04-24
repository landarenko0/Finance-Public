package com.example.finance.ui.screens.statistics

sealed interface StatisticsUiEvent {

    data object OnAccountPickerClick : StatisticsUiEvent

    data class OnAccountSelected(val accountId: Int) : StatisticsUiEvent

    data object OnDialogDismiss : StatisticsUiEvent

    data class OnOperationTypeTabSelected(val selectedTabIndex: Int) : StatisticsUiEvent

    data class OnPeriodTabSelected(val selectedTabIndex: Int) : StatisticsUiEvent
}