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
import com.example.finance.ui.navigation.AppScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
        viewModelScope.launch {
            val operationsByCategoryScreenParameters = savedStateHandle.toRoute<AppScreens.OperationsByCategoryScreen>()

            val categoryId = operationsByCategoryScreenParameters.categoryId
            val accountId = operationsByCategoryScreenParameters.accountId
            val operationType = operationsByCategoryScreenParameters.operationType

            val periodArr = operationsByCategoryScreenParameters.period.split(":")
            val period = Period(
                startDate = LocalDate.parse(periodArr[0]),
                endDate = LocalDate.parse(periodArr[1])
            )

            when (operationType) {
                OperationType.EXPENSES, OperationType.INCOME -> {
                    categoryId?.let {
                        val operations = when (accountId) {
                            0 -> {
                                // TODO: Получение всех операций по категории и периоду
                                emptyList()
                            }

                            else -> {
                                operationInteractor
                                    .getOperationsByCategoryId(
                                        categoryId = categoryId,
                                        accountId = accountId,
                                        period = period
                                    )
                                    .first()
                            }
                        }

                        _uiState.update {
                            it.copy(
                                categoryName = categoryInteractor.getCategoryById(categoryId).name,
                                transactionsSum = operations.sumOf { operation -> operation.sum },
                                period = period,
                                operationType = operationType,
                                accountId = accountId,
                                categoryId = categoryId,
                                details = OperationsByCategoryDetails.Operations(operations)
                            )
                        }
                    }
                }

                else -> {
                    val transfers = when (accountId) {
                        0 -> {
                            // TODO: Получение всех переводов по периоду
                            emptyList()
                        }

                        else -> {
                            transferInteractor
                                .getTransfersByAccountsIdAndPeriod(
                                    operationType = operationType,
                                    accountId = accountId,
                                    period = period
                                )
                                .first()
                        }
                    }

                    _uiState.update {
                        it.copy(
                            categoryName = when (operationType) {
                                OperationType.OUTCOME_TRANSFER -> "Исходящие переводы"
                                OperationType.INCOME_TRANSFER -> "Входящие переводы"
                                OperationType.TRANSFER -> "Переводы"
                                else -> ""
                            },
                            transactionsSum = transfers.sumOf { transfer -> transfer.sum },
                            period = period,
                            operationType = operationType,
                            accountId = accountId,
                            categoryId = categoryId,
                            details = OperationsByCategoryDetails.Transfers(transfers)
                        )
                    }
                }
            }
        }
    }
}