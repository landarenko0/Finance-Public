package com.example.finance.data.repository

import com.example.finance.data.local.dao.OperationDao
import com.example.finance.data.local.entities.mappers.domainToDb.toDb
import com.example.finance.domain.entities.GroupedCategories
import com.example.finance.domain.entities.Operation
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.Period
import com.example.finance.data.local.entities.mappers.dbToDomain.toDomain
import com.example.finance.domain.repository.OperationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OperationRepositoryImpl(
    private val operationDao: OperationDao,
    private val dispatcher: CoroutineDispatcher
) : OperationRepository {

    override fun getOperationsByAccountAndTypeAndPeriod(
        accountId: Int,
        operationType: OperationType,
        period: Period
    ): Flow<List<Operation>> = operationDao.getOperationsByAccountAndTypeAndPeriod(
        accountId = accountId,
        operationType = operationType,
        startDate = period.startDate,
        endDate = period.endDate
    ).map { it.toDomain() }.flowOn(dispatcher)

    override fun getOperationsByTypeAndPeriod(
        operationType: OperationType,
        period: Period
    ): Flow<List<Operation>> = operationDao.getOperationsByTypeAndPeriod(
        operationType = operationType,
        startDate = period.startDate,
        endDate = period.endDate
    ).map { it.toDomain() }.flowOn(dispatcher)

    override fun getGroupedCategoriesByAccountAndPeriod(
        accountId: Int,
        period: Period
    ): Flow<List<GroupedCategories>> = operationDao.getGroupedCategoriesByAccountId(
        accountId = accountId,
        startDate = period.startDate,
        endDate = period.endDate
    ).map { it.toDomain() }.flowOn(dispatcher)

    override fun getAllGroupedCategoriesByPeriod(
        period: Period
    ): Flow<List<GroupedCategories>> = operationDao.getAllGroupedCategories(
        startDate = period.startDate,
        endDate = period.endDate
    ).map { it.toDomain() }.flowOn(dispatcher)

    override fun getOperationsByCategoryAndAccountAndPeriod(
        categoryId: Int,
        accountId: Int,
        period: Period
    ): Flow<List<Operation>> = operationDao.getOperationsByCategoryAndAccount(
        categoryId = categoryId,
        accountId = accountId,
        startDate = period.startDate,
        endDate = period.endDate
    ).map { it.toDomain() }.flowOn(dispatcher)

    override fun getOperationsByCategoryAndPeriod(
        categoryId: Int,
        period: Period
    ): Flow<List<Operation>> = operationDao.getOperationsByCategory(
        categoryId = categoryId,
        startDate = period.startDate,
        endDate = period.endDate
    ).map { it.toDomain() }.flowOn(dispatcher)

    override fun getAll(): Flow<List<Operation>> =
        operationDao.getAll().map { it.toDomain() }.flowOn(dispatcher)

    override suspend fun getObjectById(objectId: Int): Operation =
        withContext(dispatcher) { operationDao.getOperationById(objectId).toDomain() }

    override suspend fun insert(obj: Operation) =
        withContext(dispatcher) { operationDao.insert(obj.toDb()) }

    override suspend fun update(obj: Operation) =
        withContext(dispatcher) { operationDao.update(obj.toDb()) }

    override suspend fun delete(obj: Operation) =
        withContext(dispatcher) { operationDao.delete(obj.toDb()) }
}
