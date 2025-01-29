package com.example.finance.ui.screens.main

sealed interface MainUiEvent {

    data object OnAccountPickerClick : MainUiEvent

    data class OnNewAccountSelected(val accountId: Int) : MainUiEvent

    data class OnNewDateRangeSelected(val dateRange: Pair<Long, Long>) : MainUiEvent

    data class OnTabSelected(val tabIndex: Int) : MainUiEvent

    data object OnDateRangeCleared : MainUiEvent

    data object OnDialogDismiss : MainUiEvent

    data object OnComposition : MainUiEvent
}