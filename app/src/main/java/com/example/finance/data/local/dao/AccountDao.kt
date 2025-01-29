package com.example.finance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.finance.data.local.entities.AccountDb
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao : BaseDao<AccountDb> {

    @Query("SELECT * FROM accountdb ORDER BY id DESC")
    fun getAll(): Flow<List<AccountDb>>

    @Query("SELECT * FROM accountdb WHERE id = :accountId")
    suspend fun getAccountById(accountId: Int): AccountDb

    @Query("DELETE FROM accountdb WHERE id = :accountId")
    suspend fun deleteAccountById(accountId: Int)

    @Query("SELECT sum(sum) FROM accountdb")
    suspend fun getAccountsTotalSum(): Long?
}