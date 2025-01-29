package com.example.finance.data.repository

import com.example.finance.data.local.dao.TransferDao
import com.example.finance.data.local.entities.mappers.TransferDomainToDbMapper
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.Period
import com.example.finance.domain.entities.Transfer
import com.example.finance.domain.entities.mappers.TransferDbExtendedToDomainMapper
import com.example.finance.domain.repository.TransferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransferRepositoryImpl(
    private val transferDao: TransferDao,
    private val transferDomainToDbMapper: TransferDomainToDbMapper,
    private val transferDbExtendedToDomainMapper: TransferDbExtendedToDomainMapper
) : TransferRepository {

    override fun getTransfersByAccountIdAndPeriod(
        accountId: Int,
        operationType: OperationType,
        period: Period
    ): Flow<List<Transfer>> = transferDao.getTransfersByAccountIdAndPeriod(
        accountId = accountId,
        operationType = operationType,
        startDate = period.startDate,
        endDate = period.endDate
    ).map { transfers ->
        transfers.map(transferDbExtendedToDomainMapper)
    }

    override fun getAll(): Flow<List<Transfer>> = transferDao.getAll().map { transfers ->
        transfers.map { transferDbExtendedToDomainMapper(it) }
    }

    override suspend fun getObjectById(objectId: Int): Transfer {
        val transferDbExtended = transferDao.getTransferById(objectId)
        return transferDbExtendedToDomainMapper(transferDbExtended)
    }

    override suspend fun insert(obj: Transfer) = transferDao.insert(transferDomainToDbMapper(obj))

    override suspend fun insertAll(objects: List<Transfer>) =
        transferDao.insertAll(objects.map(transferDomainToDbMapper))

    override suspend fun update(obj: Transfer) = transferDao.update(transferDomainToDbMapper(obj))

    override suspend fun delete(obj: Transfer) = transferDao.delete(transferDomainToDbMapper(obj))

    override suspend fun deleteObjectById(objectId: Int) = transferDao.deleteTransferById(objectId)
}