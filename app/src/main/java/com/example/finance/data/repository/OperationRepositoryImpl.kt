package com.example.finance.data.repository

import com.example.finance.data.local.dao.OperationDao
import com.example.finance.data.local.entities.mappers.OperationDomainToDbMapper
import com.example.finance.domain.entities.GroupedCategories
import com.example.finance.domain.entities.Operation
import com.example.finance.domain.entities.Period
import com.example.finance.domain.entities.mappers.OperationDbExtendedToDomainMapper
import com.example.finance.domain.repository.OperationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OperationRepositoryImpl(
    private val operationDao: OperationDao,
    private val operationDomainToDbMapper: OperationDomainToDbMapper,
    private val operationDbExtendedToDomainMapper: OperationDbExtendedToDomainMapper
) : OperationRepository {

    override fun getGroupedCategoriesByAccountId(
        accountId: Int,
        period: Period
    ): Flow<List<GroupedCategories>> = operationDao.getGroupedCategoriesByAccountId(
        accountId = accountId,
        startDate = period.startDate,
        endDate = period.endDate
    )

    override fun getAllGroupedCategories(
        period: Period
    ): Flow<List<GroupedCategories>>  = operationDao.getAllGroupedCategories(
        startDate = period.startDate,
        endDate = period.endDate
    )

    override fun getOperationsByCategoryId(
        categoryId: Int,
        accountId: Int,
        period: Period
    ): Flow<List<Operation>> = operationDao.getOperationsByCategory(
        categoryId = categoryId,
        accountId = accountId,
        startDate = period.startDate,
        endDate = period.endDate
    ).map { operations ->
        operations.map(operationDbExtendedToDomainMapper)
    }

    override fun getAll(): Flow<List<Operation>> = operationDao.getAll().map { operations ->
        operations.map(operationDbExtendedToDomainMapper)
    }

    override suspend fun getObjectById(objectId: Int): Operation {
        val operationDbExtended = operationDao.getOperationById(objectId)
        return operationDbExtendedToDomainMapper(operationDbExtended)
    }

    override suspend fun insert(obj: Operation) =
        operationDao.insert(operationDomainToDbMapper(obj))

    override suspend fun insertAll(objects: List<Operation>) =
        operationDao.insertAll(objects.map(operationDomainToDbMapper))

    override suspend fun update(obj: Operation) = operationDao.update(operationDomainToDbMapper(obj))

    override suspend fun delete(obj: Operation) =
        operationDao.delete(operationDomainToDbMapper(obj))

    override suspend fun deleteObjectById(objectId: Int) =
        operationDao.deleteOperationById(objectId)

    override suspend fun deleteOperationsByAccountId(accountId: Int) =
        operationDao.deleteOperationsByAccountId(accountId)
}