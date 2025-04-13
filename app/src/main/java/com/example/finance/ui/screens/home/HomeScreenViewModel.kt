package com.example.finance.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.domain.entities.Account
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.Period
import com.example.finance.domain.usecases.AccountInteractor
import com.example.finance.domain.usecases.OperationInteractor
import com.example.finance.utils.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val operationInteractor: OperationInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _event = Channel<HomeEvent>()
    val event = _event.receiveAsFlow()

    private val selectedPeriodString
        get() = with(_uiState.value.selectedPeriod) { "$startDate:$endDate" }

    private var operationsCollectorJob: Job? = null
    private var accountCollectorJob: Job? = null

    init {
        accountInteractor.getSavedAccountId()
            .map { it ?: 0 }
            .onEach { accountId ->
                updateSelectedAccount(accountId)
                updateAccountOperations(accountId, _uiState.value.selectedPeriod)
            }
            .launchIn(viewModelScope)

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

    fun onUiEvent(uiEvent: HomeUiEvent) {
        when (uiEvent) {
            HomeUiEvent.OnAccountPickerClick -> {
                _uiState.update { it.copy(showAccountPickerDialog = true) }
            }

            HomeUiEvent.OnDialogDismiss -> {
                _uiState.update {
                    it.copy(
                        showAccountPickerDialog = false,
                        showDateRangePickerDialog = false
                    )
                }
            }

            is HomeUiEvent.OnNewAccountSelected -> {
                if (uiEvent.accountId != _uiState.value.selectedAccount.id) {
                    updateSelectedAccount(uiEvent.accountId)
                    updateSavedAccountId(uiEvent.accountId)
                    updateAccountOperations(
                        uiEvent.accountId,
                        _uiState.value.selectedPeriod
                    )
                }

                _uiState.update { it.copy(showAccountPickerDialog = false) }
            }

            is HomeUiEvent.OnNewDateRangeSelected -> onNewDateRangeSelected(uiEvent.dateRange)

            is HomeUiEvent.OnTabSelected -> {
                updatePeriodTab(uiEvent.tabIndex)
                updateAccountOperations(
                    _uiState.value.selectedAccount.id,
                    _uiState.value.selectedPeriod
                )
            }

            HomeUiEvent.OnFloatingButtonClick -> {
                viewModelScope.launch {
                    _event.send(HomeEvent.NavigateToCreateOperationScreen(_uiState.value.selectedAccount.id))
                }
            }

            is HomeUiEvent.OnGroupedCategoryClick -> {
                viewModelScope.launch {
                    _event.send(HomeEvent.NavigateToOperationsByCategoryScreen(
                        uiEvent.operationType,
                        _uiState.value.selectedAccount.id,
                        uiEvent.categoryId,
                        selectedPeriodString
                    ))
                }
            }
        }
    }

    private fun updateSelectedAccount(accountId: Int) {
        viewModelScope.launch {
            accountCollectorJob?.cancel()

            val accountFlow = when (accountId) {
                0 -> accountInteractor.flowTotalAccount()

                else -> accountInteractor.flowAccountById(accountId)
                    .onEach { if (it == null) updateSavedAccountId(0) }
                    .filterNotNull()
            }

            accountCollectorJob = accountFlow
                .onEach { account -> _uiState.update { it.copy(selectedAccount = account) } }
                .cancellable()
                .launchIn(viewModelScope)
        }
    }

    private fun updateSavedAccountId(accountId: Int) {
        viewModelScope.launch { accountInteractor.updateSavedAccountId(accountId) }
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

        _uiState.update { it.copy(selectedPeriod = period) }
    }

    private fun updatePeriod(startDate: LocalDate, endDate: LocalDate) {
        Period(startDate = startDate, endDate = endDate).also { period ->
            _uiState.update { it.copy(selectedPeriod = period) }
        }
    }

    private fun updateAccountOperations(accountId: Int, period: Period) {
        viewModelScope.launch {
            operationsCollectorJob?.cancel()

            val groupedCategoriesFlow = when (accountId) {
                0 -> operationInteractor.getAllGroupedCategories(period)
                else -> operationInteractor.getGroupedCategories(accountId, period)
            }

            operationsCollectorJob = groupedCategoriesFlow
                .onEach { operations ->
                    _uiState.update { state ->
                        state.copy(
                            accountOperations = operations,
                            expensesSum = operations.filter {
                                it.operationType == OperationType.EXPENSES || it.operationType == OperationType.OUTCOME_TRANSFER
                            }.sumOf { it.totalSum },
                            incomeSum = operations.filter {
                                it.operationType == OperationType.INCOME || it.operationType == OperationType.INCOME_TRANSFER
                            }.sumOf { it.totalSum },
                            showDonutChart = true
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .cancellable()
                .launchIn(viewModelScope)
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

                    _uiState.update { it.copy(selectedTabIndex = selectedTabIndex) }
                    viewModelScope.launch { _event.send(HomeEvent.ClearDateRange) }
                }
            }
        }
    }

    private fun onNewDateRangeSelected(dateRange: Pair<Long, Long>) {
        val startDate = _uiState.value.selectedPeriod.startDate
        val endDate = _uiState.value.selectedPeriod.endDate

        val selectedStartDate = dateRange.first.toLocalDate()
        val selectedEndDate = dateRange.second.toLocalDate()

        val dateRangesAreSame = selectedStartDate == startDate && selectedEndDate == endDate
        val currentTabIndex = _uiState.value.selectedTabIndex

        if (dateRangesAreSame) {
            when (currentTabIndex) {
                PERIOD_TAB_INDEX -> _uiState.update { it.copy(showDateRangePickerDialog = false) }

                else -> {
                    updatePeriodTab(currentTabIndex)
                    viewModelScope.launch { _event.send(HomeEvent.ClearDateRange) }
                }
            }
        } else {
            updatePeriod(selectedStartDate, selectedEndDate)
            _uiState.update { it.copy(selectedTabIndex = PERIOD_TAB_INDEX) }
        }

        updateAccountOperations(_uiState.value.selectedAccount.id, _uiState.value.selectedPeriod)
        _uiState.update { it.copy(showDateRangePickerDialog = false) }
    }
}