package com.example.finance.data.repository

import com.example.finance.data.local.dao.TransferDao
import com.example.finance.data.local.entities.mappers.toDb
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.Period
import com.example.finance.domain.entities.Transfer
import com.example.finance.domain.entities.mappers.toDomain
import com.example.finance.domain.repository.TransferRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TransferRepositoryImpl(
    private val transferDao: TransferDao,
    private val dispatcher: CoroutineDispatcher
) : TransferRepository {

    override fun getTransfersByAccountAndTypeAndPeriod(
        accountId: Int,
        operationType: OperationType,
        period: Period
    ): Flow<List<Transfer>> = transferDao.getTransfersByAccountIdAndPeriod(
        accountId = accountId,
        operationType = operationType,
        startDate = period.startDate,
        endDate = period.endDate
    ).map { it.toDomain() }.flowOn(dispatcher)

    override fun getTransfersByPeriod(
        period: Period
    ): Flow<List<Transfer>> = transferDao.getTransferByPeriod(
        startDate = period.startDate,
        endDate = period.endDate
    ).map { it.toDomain() }.flowOn(dispatcher)

    override fun getAll(): Flow<List<Transfer>> =
        transferDao.getAll().map { it.toDomain() }.flowOn(dispatcher)

    override suspend fun getObjectById(objectId: Int): Transfer =
        withContext(dispatcher) { transferDao.getTransferById(objectId).toDomain() }

    override suspend fun insert(obj: Transfer) =
        withContext(dispatcher) { transferDao.insert(obj.toDb()) }

    override suspend fun update(obj: Transfer) =
        withContext(dispatcher) { transferDao.update(obj.toDb()) }

    override suspend fun delete(obj: Transfer) =
        withContext(dispatcher) { transferDao.delete(obj.toDb()) }
}