package com.example.finance.ui.screens.main

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.domain.entities.Account
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.Period
import com.example.finance.domain.usecases.AccountInteractor
import com.example.finance.domain.usecases.OperationInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

val ACCOUNT_ID = intPreferencesKey("account_id")

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val operationInteractor: OperationInteractor,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun onUiEvent(uiEvent: MainUiEvent) {
        when (uiEvent) {
            MainUiEvent.OnAccountPickerClick -> {
                _uiState.update { it.copy(showAccountPickerDialog = true) }
            }

            MainUiEvent.OnDateRangeCleared -> {
                _uiState.update { it.copy(clearSelectedDateRange = false) }
            }

            MainUiEvent.OnDialogDismiss -> {
                _uiState.update {
                    it.copy(
                        showAccountPickerDialog = false,
                        showDateRangePickerDialog = false
                    )
                }
            }

            MainUiEvent.OnComposition -> {
                setInitialAccount()
                getAllAccounts()
            }

            is MainUiEvent.OnNewAccountSelected -> {
                if (uiEvent.accountId != _uiState.value.selectedAccount.id) {
                    updateSelectedAccount(uiEvent.accountId)
                    updateAccountIdInDataStore(uiEvent.accountId)
                } else {
                    _uiState.update { it.copy(showAccountPickerDialog = false) }
                }
            }

            is MainUiEvent.OnNewDateRangeSelected -> updateDateRangeDialog(uiEvent.dateRange)

            is MainUiEvent.OnTabSelected -> updatePeriodTab(uiEvent.tabIndex)
        }
    }

    private fun setInitialAccount() {
        viewModelScope.launch {
            try {
                val preferences = dataStore.data.first()
                val accountId = preferences[ACCOUNT_ID] ?: 0
                updateSelectedAccount(accountId)
            } catch (ex: Exception) {
                updateSelectedAccount(0)
            }
        }
    }

    private fun getAllAccounts() {
        viewModelScope.launch {
            val accounts = accountInteractor
                .getAllAccounts()
                .map { accounts ->
                    accounts
                        .reversed()
                        .toMutableList()
                        .also {
                            it.add(
                                index = 0,
                                element = Account(
                                    id = 0,
                                    name = "Общий",
                                    sum = accounts.sumOf { account -> account.sum }
                                )
                            )
                        }
                        .toList()
                }
                .first()

            _uiState.update { it.copy(accounts = accounts) }
        }
    }

    private fun updateSelectedAccount(accountId: Int) {
        viewModelScope.launch {
            val selectedAccount = when (accountId) {
                0 -> Account(
                    id = 0,
                    name = "Общий",
                    sum = accountInteractor.getAccountsTotalSum() ?: 0
                )

                else -> {
                    try {
                        accountInteractor.getAccountById(accountId)
                    } catch (ex: Exception) {
                        Account(
                            id = 0,
                            name = "Общий",
                            sum = accountInteractor.getAccountsTotalSum() ?: 0
                        )
                    }
                }
            }

            _uiState.update {
                it.copy(
                    selectedAccount = selectedAccount,
                    showAccountPickerDialog = false
                )
            }

            updateSelectedAccountOperations(selectedAccount.id, _uiState.value.selectedPeriod)
        }
    }

    private fun updateAccountIdInDataStore(accountId: Int) {
        viewModelScope.launch {
            try {
                dataStore.edit { settings ->
                    settings[ACCOUNT_ID] = accountId
                }
            } catch (_: Exception) {}
        }
    }

    private fun updatePeriod(selectedTabIndex: Int) {
        val period = when (selectedTabIndex) {
            CURRENT_DATE_TAB_INDEX -> {
                val now = LocalDate.now()

                Period(
                    startDate = now,
                    endDate = now
                )
            }

            CURRENT_WEEK_TAB_INDEX -> {
                val now = LocalDate.now()

                Period(
                    startDate = now.with(DayOfWeek.MONDAY),
                    endDate = now.with(DayOfWeek.SUNDAY)
                )
            }

            CURRENT_MONTH_TAB_INDEX -> {
                val now = LocalDate.now()

                Period(
                    startDate = now.withDayOfMonth(1),
                    endDate = now.withDayOfMonth(now.lengthOfMonth())
                )
            }

            else -> _uiState.value.selectedPeriod
        }

        _uiState.update {
            it.copy(
                selectedPeriod = period,
                periodString = "${period.startDate}:${period.endDate}"
            )
        }

        updateSelectedAccountOperations(_uiState.value.selectedAccount.id, period)
    }

    private fun updatePeriod(startDate: LocalDate, endDate: LocalDate) {
        val period = Period(startDate = startDate, endDate = endDate)

        _uiState.update {
            it.copy(
                selectedPeriod = period,
                periodString = "${period.startDate}:${period.endDate}"
            )
        }

        updateSelectedAccountOperations(_uiState.value.selectedAccount.id, period)
    }

    private fun updateSelectedAccountOperations(accountId: Int, period: Period) {
        viewModelScope.launch {
            val groupedCategories = when (accountId) {
                0 -> operationInteractor.getAllGroupedCategories(period).first()
                else -> operationInteractor.getGroupedCategories(accountId, period).first()
            }

            _uiState.update { state ->
                state.copy(
                    selectedAccountOperations = groupedCategories,
                    expensesSum = groupedCategories.filter {
                        it.operationType == OperationType.EXPENSES || it.operationType == OperationType.OUTCOME_TRANSFER
                    }.sumOf { it.totalSum },
                    incomeSum = groupedCategories.filter {
                        it.operationType == OperationType.INCOME || it.operationType == OperationType.INCOME_TRANSFER
                    }.sumOf { it.totalSum },
                    showDonutChart = true
                )
            }
        }
    }

    private fun updatePeriodTab(selectedTabIndex: Int) {
        when (selectedTabIndex) {
            PERIOD_TAB_INDEX -> {
                _uiState.update { it.copy(showDateRangePickerDialog = true) }
            }

            else -> {
                if (selectedTabIndex != _uiState.value.selectedTabIndex) {
                    updatePeriod(selectedTabIndex)

                    _uiState.update {
                        it.copy(
                            selectedTabIndex = selectedTabIndex,
                            clearSelectedDateRange = true
                        )
                    }
                }
            }
        }
    }

    private fun updateDateRangeDialog(dateRange: Pair<Long, Long>) {
        val startDate = _uiState.value.selectedPeriod.startDate
        val endDate = _uiState.value.selectedPeriod.endDate

        val selectedStartDate = Instant.ofEpochMilli(dateRange.first).atZone(ZoneId.systemDefault()).toLocalDate()
        val selectedEndDate = Instant.ofEpochMilli(dateRange.second).atZone(ZoneId.systemDefault()).toLocalDate()

        if (selectedStartDate != startDate || selectedEndDate != endDate) {
            updatePeriod(selectedStartDate, selectedEndDate)

            _uiState.update {
                it.copy(
                    showDateRangePickerDialog = false,
                    selectedTabIndex = PERIOD_TAB_INDEX
                )
            }
        }
    }
}