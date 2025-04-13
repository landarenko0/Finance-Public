package com.example.finance.ui.screens.home

import com.example.finance.domain.entities.OperationType

sealed interface HomeUiEvent {

    data object OnAccountPickerClick : HomeUiEvent

    data class OnNewAccountSelected(val accountId: Int) : HomeUiEvent

    data class OnNewDateRangeSelected(val dateRange: Pair<Long, Long>) : HomeUiEvent

    data class OnTabSelected(val tabIndex: Int) : HomeUiEvent

    data object OnDialogDismiss : HomeUiEvent

    data object OnFloatingButtonClick : HomeUiEvent

    data class OnGroupedCategoryClick(
        val operationType: OperationType,
        val categoryId: Int?
    ) : HomeUiEvent
}