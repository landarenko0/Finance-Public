package com.example.finance.data.repository

import com.example.finance.data.local.dao.AccountDao
import com.example.finance.data.local.entities.mappers.AccountDomainToDbMapper
import com.example.finance.domain.entities.Account
import com.example.finance.domain.entities.mappers.AccountDbToDomainMapper
import com.example.finance.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepositoryImpl(
    private val accountDao: AccountDao,
    private val accountDbToDomainMapper: AccountDbToDomainMapper,
    private val accountDomainToDbMapper: AccountDomainToDbMapper
) : AccountRepository {

    override fun getAll(): Flow<List<Account>> = accountDao.getAll().map { accounts ->
        accounts.map(accountDbToDomainMapper)
    }

    override suspend fun getObjectById(objectId: Int): Account {
        val accountDb = accountDao.getAccountById(objectId)
        return accountDbToDomainMapper(accountDb)
    }

    override suspend fun insert(obj: Account) = accountDao.insert(accountDomainToDbMapper(obj))

    override suspend fun insertAll(objects: List<Account>) =
        accountDao.insertAll(objects.map(accountDomainToDbMapper))

    override suspend fun update(obj: Account) = accountDao.update(accountDomainToDbMapper(obj))

    override suspend fun delete(obj: Account) = accountDao.delete(accountDomainToDbMapper(obj))

    override suspend fun deleteObjectById(objectId: Int) = accountDao.deleteAccountById(objectId)

    override suspend fun getAccountsTotalSum(): Long? = accountDao.getAccountsTotalSum()
}