package com.example.finance.ui.screens.home

import com.example.finance.domain.entities.OperationType

sealed interface HomeEvent {

    data object ClearDateRange : HomeEvent

    data class NavigateToCreateOperationScreen(val accountId: Int) : HomeEvent

    data class NavigateToOperationsByCategoryScreen(
        val operationType: OperationType,
        val accountId: Int,
        val categoryId: Int?,
        val period: String
    ) : HomeEvent
}