package com.example.finance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.finance.data.local.entities.TransferDb
import com.example.finance.data.local.entities.TransferDbExtended
import com.example.finance.domain.entities.OperationType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TransferDao : BaseDao<TransferDb> {

    @Transaction
    @Query("SELECT * FROM transferdb ORDER BY id DESC")
    fun getAll(): Flow<List<TransferDbExtended>>

    @Transaction
    @Query("SELECT * FROM transferdb WHERE id = :transferId")
    suspend fun getTransferById(transferId: Int): TransferDbExtended

    @Transaction
    @Query(GET_TRANSFERS_BY_ACCOUNT_ID_AND_PERIOD_QUERY)
    fun getTransfersByAccountIdAndPeriod(
        accountId: Int,
        operationType: OperationType,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<TransferDbExtended>>

    @Query("DELETE FROM transferdb WHERE id = :transferId")
    suspend fun deleteTransferById(transferId: Int)

    companion object {
        private const val GET_TRANSFERS_BY_ACCOUNT_ID_AND_PERIOD_QUERY: String = """
            SELECT
                id,
                fromAccountId,
                toAccountId,
                sum,
                date,
                comment
            FROM
                (
                    SELECT
                        id,
                        fromAccountId,
                        toAccountId,
                        sum,
                        date,
                        comment,
                        CASE
                            WHEN fromAccountId = :accountId THEN 'OUTCOME_TRANSFER'
                            ELSE 'INCOME_TRANSFER'
                        END AS transferType
                    FROM
                        transferdb
                    WHERE
                        (
                            fromAccountId = :accountId
                            OR toAccountId = :accountId
                        )
                )
            WHERE
                transferType = :operationType
                AND date BETWEEN :startDate AND :endDate
        """
    }
}