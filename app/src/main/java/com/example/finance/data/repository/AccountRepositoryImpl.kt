package com.example.finance.data.repository

import com.example.finance.data.local.dao.AccountDao
import com.example.finance.data.local.entities.mappers.domainToDb.toDb
import com.example.finance.domain.entities.Account
import com.example.finance.data.local.entities.mappers.dbToDomain.toDomain
import com.example.finance.domain.repository.AccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AccountRepositoryImpl(
    private val accountDao: AccountDao,
    private val dispatcher: CoroutineDispatcher
) : AccountRepository {

    override fun getAll(): Flow<List<Account>> =
        accountDao.getAll().map { it.toDomain() }.flowOn(dispatcher)

    override suspend fun getObjectById(objectId: Int): Account =
        withContext(dispatcher) { accountDao.getAccountById(objectId).toDomain() }

    override suspend fun insert(obj: Account) =
        withContext(dispatcher) { accountDao.insert(obj.toDb()) }

    override suspend fun update(obj: Account) =
        withContext(dispatcher) { accountDao.update(obj.toDb()) }

    override suspend fun delete(obj: Account) =
        withContext(dispatcher) { accountDao.delete(obj.toDb()) }

    override suspend fun getAccountsTotalSum(): Long? =
        withContext(dispatcher) { accountDao.getAccountsTotalSum() }

    override fun flowAccountById(accountId: Int): Flow<Account?> =
        accountDao.flowAccountById(accountId).map { it?.toDomain() }.flowOn(dispatcher)

    override fun flowTotalAccount(): Flow<Account> =
        accountDao.flowTotalAccount().map { it.toDomain() }.flowOn(dispatcher)
}