package com.example.finance.ui.screens.home

import com.example.finance.domain.entities.Account
import com.example.finance.domain.entities.GroupedCategories
import com.example.finance.domain.entities.Period
import java.time.LocalDate

data class HomeUiState(
    val selectedAccount: Account = Account(id = 0, name = "", balance = 0),
    val accountOperations: List<GroupedCategories> = emptyList(),
    val expensesSum: Long = 0,
    val incomeSum: Long = 0,
    val selectedTabIndex: Int = CURRENT_DATE_TAB_INDEX,
    val selectedPeriod: Period = Period(startDate = LocalDate.now(), endDate = LocalDate.now()),
    val accounts: List<Account> = emptyList(),
    val showDonutChart: Boolean = false,
    val showAccountPickerDialog: Boolean = false,
    val showDateRangePickerDialog: Boolean = false
)