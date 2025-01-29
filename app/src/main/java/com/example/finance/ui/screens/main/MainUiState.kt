package com.example.finance.ui.screens.main

import com.example.finance.domain.entities.Account
import com.example.finance.domain.entities.GroupedCategories
import com.example.finance.domain.entities.Period
import java.time.LocalDate

data class MainUiState(
    val selectedAccount: Account = Account(id = -1, name = "", sum = 0),
    val selectedAccountOperations: List<GroupedCategories> = emptyList(),
    val expensesSum: Long = 0L,
    val incomeSum: Long = 0L,
    val selectedTabIndex: Int = CURRENT_DATE_TAB_INDEX,
    val selectedPeriod: Period = Period(startDate = LocalDate.now(), endDate = LocalDate.now()),
    val accounts: List<Account> = emptyList(),
    val showDonutChart: Boolean = false,
    val showAccountPickerDialog: Boolean = false,
    val showDateRangePickerDialog: Boolean = false,
    val clearSelectedDateRange: Boolean = false,
    val periodString: String = "${selectedPeriod.startDate}:${selectedPeriod.endDate}"
)