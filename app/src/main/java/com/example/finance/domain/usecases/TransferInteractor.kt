package com.example.finance.domain.usecases

import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.Period
import com.example.finance.domain.entities.Transfer
import com.example.finance.domain.repository.TransferRepository
import kotlinx.coroutines.flow.Flow

class TransferInteractor(
    val getAllTransfers: GetAllUseCase<Transfer>,
    val getTransferById: GetObjectByIdUseCase<Transfer>,
    val addTransfer: InsertUseCase<Transfer>,
    val addAllTransfers: InsertAllUseCase<Transfer>,
    val updateTransfer: UpdateUseCase<Transfer>,
    val deleteTransfer: DeleteUseCase<Transfer>,
    val deleteTransferById: DeleteObjectByIdUseCase<Transfer>,
    val getTransfersByAccountsIdAndPeriod: GetTransfersByAccountsIdAndPeriodUseCase
)

class GetTransfersByAccountsIdAndPeriodUseCase(private val repository: TransferRepository) {

    operator fun invoke(
        accountId: Int,
        operationType: OperationType,
        period: Period
    ): Flow<List<Transfer>> =
        repository.getTransfersByAccountIdAndPeriod(accountId, operationType, period)
}