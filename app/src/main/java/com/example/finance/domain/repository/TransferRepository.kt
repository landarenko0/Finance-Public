package com.example.finance.domain.repository

import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.Period
import com.example.finance.domain.entities.Transfer
import kotlinx.coroutines.flow.Flow

interface TransferRepository : BaseRepository<Transfer> {

    fun getTransfersByAccountAndTypeAndPeriod(
        accountId: Int,
        operationType: OperationType,
        period: Period
    ): Flow<List<Transfer>>

    fun getTransfersByPeriod(period: Period): Flow<List<Transfer>>
}