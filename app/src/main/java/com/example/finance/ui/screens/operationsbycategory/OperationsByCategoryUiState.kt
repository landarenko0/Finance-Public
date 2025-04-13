package com.example.finance.ui.screens.operationsbycategory

import com.example.finance.domain.entities.Operation
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.Period
import com.example.finance.domain.entities.Transfer
import java.time.LocalDate

data class OperationsByCategoryUiState(
    val period: Period = Period(startDate = LocalDate.now(), endDate = LocalDate.now()),
    val operationType: OperationType = OperationType.EXPENSES,
    val accountId: Int = 0,
    val categoryId: Int? = null,
    val categoryName: String = "",
    val transactionsSum: Long = 0,
    val details: OperationsByCategoryDetails = OperationsByCategoryDetails.Initial
)

sealed interface OperationsByCategoryDetails {

    data object Initial : OperationsByCategoryDetails

    data class Operations(
        val operations: Map<LocalDate, List<Operation>> = emptyMap()
    )  : OperationsByCategoryDetails

    data class Transfers(
        val transfers: Map<LocalDate, List<Transfer>> = emptyMap()
    )  : OperationsByCategoryDetails
}