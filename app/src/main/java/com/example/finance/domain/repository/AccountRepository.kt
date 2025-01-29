package com.example.finance.domain.repository

import com.example.finance.domain.entities.Account

interface AccountRepository : BaseRepository<Account> {

    suspend fun getAccountsTotalSum(): Long?
}