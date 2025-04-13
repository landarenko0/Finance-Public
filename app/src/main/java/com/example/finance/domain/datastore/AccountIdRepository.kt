package com.example.finance.domain.datastore

import kotlinx.coroutines.flow.Flow

interface AccountIdRepository {

    val data: Flow<Int?>

    suspend fun updateAccountId(accountId: Int)
}