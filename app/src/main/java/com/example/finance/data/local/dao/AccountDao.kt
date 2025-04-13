package com.example.finance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.finance.data.local.entities.AccountDb
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao : BaseDao<AccountDb> {

    @Query("SELECT * FROM accountdb")
    fun getAll(): Flow<List<AccountDb>>

    @Query("SELECT * FROM accountdb WHERE id = :accountId")
    suspend fun getAccountById(accountId: Int): AccountDb

    @Query("SELECT sum(sum) FROM accountdb")
    suspend fun getAccountsTotalSum(): Long?

    @Query("SELECT * FROM accountdb WHERE id = :accountId")
    fun flowAccountById(accountId: Int): Flow<AccountDb?>

    @Query(FLOW_TOTAL_ACCOUNT_QUERY)
    fun flowTotalAccount(): Flow<AccountDb>

    companion object {
        private const val FLOW_TOTAL_ACCOUNT_QUERY = """
            SELECT
                0 AS id,
                'Общий' AS name,
                CASE
                    WHEN totalSum IS NULL THEN 0
                    ELSE totalSum
                END AS sum
            FROM
                (
                    SELECT
                        sum(sum) AS totalSum
                    FROM accountdb
                )
        """
    }
}