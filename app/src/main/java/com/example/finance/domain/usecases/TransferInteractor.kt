package com.example.finance.domain.usecases

import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.Period
import com.example.finance.domain.entities.Transfer
import com.example.finance.domain.repository.TransferRepository
import kotlinx.coroutines.flow.Flow

class TransferInteractor(
    val getTransferById: GetObjectByIdUseCase<Transfer>,
    val addTransfer: InsertUseCase<Transfer>,
    val updateTransfer: UpdateUseCase<Transfer>,
    val deleteTransfer: DeleteUseCase<Transfer>,
    val getTransfersByAccountAndPeriod: GetTransfersByAccountsIdAndPeriodUseCase,
    val getTransfersByPeriod: GetTransferByPeriodUseCase
)

class GetTransfersByAccountsIdAndPeriodUseCase(private val repository: TransferRepository) {

    operator fun invoke(
        accountId: Int,
        operationType: OperationType,
        period: Period
    ): Flow<List<Transfer>> =
        repository.getTransfersByAccountAndTypeAndPeriod(accountId, operationType, period)
}

class GetTransferByPeriodUseCase(private val repository: TransferRepository) {

    operator fun invoke(
        period: Period
    ): Flow<List<Transfer>> = repository.getTransfersByPeriod(period)
}