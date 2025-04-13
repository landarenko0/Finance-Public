package com.example.finance.domain.repository

import com.example.finance.domain.entities.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository : BaseRepository<Account> {

    suspend fun getAccountsTotalSum(): Long?

    fun flowAccountById(accountId: Int): Flow<Account?>

    fun flowTotalAccount(): Flow<Account>
}