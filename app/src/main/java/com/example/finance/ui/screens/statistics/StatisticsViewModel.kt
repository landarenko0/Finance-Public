package com.example.finance.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.domain.entities.Account
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.usecases.AccountInteractor
import com.example.finance.domain.usecases.OperationInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.Month
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val operationInteractor: OperationInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private var accountCollectorJob: Job? = null
    private var operationsCollectorJob: Job? = null

    init {
        updateSelectedAccount(0)
        updateAccountOperations(0)

        accountInteractor
            .getAllAccounts()
            .map { accounts ->
                accounts
                    .toMutableList()
                    .also {
                        it.add(
                            index = 0,
                            element = Account(
                                id = 0,
                                name = "Общий",
                                balance = accounts.sumOf { account -> account.balance }
                            )
                        )
                    }
                    .toList()
            }
            .onEach { accounts -> _uiState.update { it.copy(accounts = accounts) } }
            .launchIn(viewModelScope)
    }

    fun onUiEvent(event: StatisticsUiEvent) {
        when (event) {
            StatisticsUiEvent.OnAccountPickerClick -> {
                _uiState.update { it.copy(showAccountPickerDialog = true) }
            }

            is StatisticsUiEvent.OnAccountSelected -> {
                if (event.accountId != _uiState.value.selectedAccount.id) {
                    updateSelectedAccount(event.accountId)
                    updateAccountOperations(event.accountId)
                }

                _uiState.update { it.copy(showAccountPickerDialog = false) }
            }

            StatisticsUiEvent.OnDialogDismiss -> {
                _uiState.update { it.copy(showAccountPickerDialog = false) }
            }

            is StatisticsUiEvent.OnOperationTypeTabSelected -> {
                onOperationTypeTabSelected(event.selectedTabIndex)
            }

            is StatisticsUiEvent.OnPeriodTabSelected -> onPeriodTabSelected(event.selectedTabIndex)
        }
    }

    private fun updateSelectedAccount(accountId: Int) {
        accountCollectorJob?.cancel()

        val accountFlow = when (accountId) {
            0 -> accountInteractor.flowTotalAccount()

            else -> accountInteractor.flowAccountById(accountId)
                .onEach { if (it == null) updateSelectedAccount(0) }
                .filterNotNull()
        }

        accountCollectorJob = accountFlow
            .onEach { account -> _uiState.update { it.copy(selectedAccount = account) } }
            .cancellable()
            .launchIn(viewModelScope)
    }

    private fun onOperationTypeTabSelected(selectedTabIndex: Int) {
        if (_uiState.value.selectedOperationTypeTabIndex != selectedTabIndex) {
            _uiState.update {
                it.copy(
                    selectedOperationTypeTabIndex = selectedTabIndex,
                    showPeriodTab = selectedTabIndex != EXPENSES_AND_INCOMES_TAB_INDEX
                )
            }
        }
    }

    private fun onPeriodTabSelected(selectedTabIndex: Int) {
        if (_uiState.value.selectedPeriodTabIndex != selectedTabIndex) {
            _uiState.update { it.copy(selectedPeriodTabIndex = selectedTabIndex) }
        }
    }

    private fun updateAccountOperations(accountId: Int) {
        operationsCollectorJob?.cancel()

        operationsCollectorJob = operationInteractor.getSumOfOperationsByMonthForTheYear(accountId)
            .onEach { map ->
                _uiState.update {
                    it.copy(
                        details = StatisticsDetails.ExpensesAndIncomes(
                            expenses = map[OperationType.EXPENSES]!!.values.toList(),
                            incomes = map[OperationType.INCOME]!!.values.toList(),
                            months = map[OperationType.EXPENSES]!!.keys.map { formatYearMonth(it) }
                        )
                    )
                }
            }
            .flowOn(Dispatchers.Default)
            .cancellable()
            .launchIn(viewModelScope)
    }

    private fun formatYearMonth(yearMonth: YearMonth): String {
        val month = when (yearMonth.month) {
            Month.JANUARY -> "Январь"
            Month.FEBRUARY -> "Февраль"
            Month.MARCH -> "Март"
            Month.APRIL -> "Апрель"
            Month.MAY -> "Май"
            Month.JUNE -> "Июнь"
            Month.JULY -> "Июль"
            Month.AUGUST -> "Август"
            Month.SEPTEMBER -> "Сентябрь"
            Month.OCTOBER -> "Октябрь"
            Month.NOVEMBER -> "Ноябрь"
            Month.DECEMBER -> "Декабрь"
        }

        return "$month ${yearMonth.year}"
    }
}