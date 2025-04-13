package com.example.finance.ui.screens.operationsbycategory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.Period
import com.example.finance.domain.usecases.CategoryInteractor
import com.example.finance.domain.usecases.OperationInteractor
import com.example.finance.domain.usecases.TransferInteractor
import com.example.finance.ui.navigation.OperationsByCategoryScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OperationsByCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    operationInteractor: OperationInteractor,
    transferInteractor: TransferInteractor,
    categoryInteractor: CategoryInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(OperationsByCategoryUiState())
    val uiState: StateFlow<OperationsByCategoryUiState> = _uiState.asStateFlow()

    init {
        val operationsByCategoryScreenParameters =
            savedStateHandle.toRoute<OperationsByCategoryScreen>()

        val categoryId = operationsByCategoryScreenParameters.categoryId
        val accountId = operationsByCategoryScreenParameters.accountId
        val operationType = operationsByCategoryScreenParameters.operationType

        val periodArr = operationsByCategoryScreenParameters.period.split(":")
        val period = Period(
            startDate = LocalDate.parse(periodArr.first()),
            endDate = LocalDate.parse(periodArr.last())
        )

        when (operationType) {
            OperationType.EXPENSES, OperationType.INCOME -> {
                categoryId?.let {
                    val operationsFlow = when (accountId) {
                        0 -> operationInteractor.getOperationsByCategory(
                            categoryId = categoryId,
                            period = period
                        )

                        else -> operationInteractor.getOperationsByCategoryAndAccount(
                            categoryId = categoryId,
                            accountId = accountId,
                            period = period
                        )
                    }

                    operationsFlow
                        .onEach { operations ->
                            _uiState.update {
                                it.copy(
                                    transactionsSum = operations.sumOf { operation -> operation.sum },
                                    details = OperationsByCategoryDetails.Operations(
                                        operations.groupBy { operations -> operations.date }
                                    )
                                )
                            }
                        }
                        .flowOn(Dispatchers.Default) // sumOf and groupBy may execute for long time
                        .launchIn(viewModelScope)

                    viewModelScope.launch {
                        _uiState.update {
                            it.copy(
                                categoryName = categoryInteractor.getCategoryById(categoryId).name,
                                period = period,
                                operationType = operationType,
                                accountId = accountId,
                                categoryId = categoryId
                            )
                        }
                    }
                }
            }

            else -> {
                val transfersFlow = when (accountId) {
                    0 -> transferInteractor.getTransfersByPeriod(period)

                    else -> transferInteractor.getTransfersByAccountAndPeriod(
                        operationType = operationType,
                        accountId = accountId,
                        period = period
                    )
                }

                transfersFlow
                    .onEach { transfers ->
                        _uiState.update {
                            it.copy(
                                transactionsSum = transfers.sumOf { transfer -> transfer.sum },
                                details = OperationsByCategoryDetails.Transfers(
                                    transfers.groupBy { transfer -> transfer.date }
                                )
                            )
                        }
                    }
                    .flowOn(Dispatchers.Default) // sumOf and groupBy may execute for long time
                    .launchIn(viewModelScope)

                _uiState.update {
                    it.copy(
                        categoryName = when (operationType) {
                            OperationType.OUTCOME_TRANSFER -> "Исходящие переводы"
                            OperationType.INCOME_TRANSFER -> "Входящие переводы"
                            OperationType.TRANSFER -> "Переводы"
                            else -> ""
                        },
                        period = period,
                        operationType = operationType,
                        accountId = accountId,
                        categoryId = categoryId
                    )
                }
            }
        }
    }
}