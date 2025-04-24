package com.example.finance.ui.screens.statistics

import com.example.finance.domain.entities.Account
import com.example.finance.domain.entities.GroupedCategories

data class StatisticsUiState(
    val accounts: List<Account> = emptyList(),
    val selectedAccount: Account = Account(0, "", 0),
    val showAccountPickerDialog: Boolean = false,
    val selectedOperationTypeTabIndex: Int = EXPENSES_AND_INCOMES_TAB_INDEX,
    val selectedPeriodTabIndex: Int = CURRENT_DATE_TAB_INDEX,
    val showPeriodTab: Boolean = false,
    val details: StatisticsDetails = StatisticsDetails.Idle
)

sealed interface StatisticsDetails {

    data object Idle : StatisticsDetails

    data class ExpensesAndIncomes(
        val expenses: List<Double>,
        val incomes: List<Double>,
        val months: List<String>
    ) : StatisticsDetails

    data class ExpensesOrIncomes(
        val groupedCategories: List<GroupedCategories>
    ) : StatisticsDetails
}