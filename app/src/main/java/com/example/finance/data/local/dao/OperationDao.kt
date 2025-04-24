package com.example.finance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.finance.data.local.entities.GroupedCategoriesDb
import com.example.finance.data.local.entities.OperationDb
import com.example.finance.data.local.entities.OperationDbExtended
import com.example.finance.domain.entities.OperationType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface OperationDao : BaseDao<OperationDb> {

    @Transaction
    @Query(GET_OPERATIONS_BY_ACCOUNT_ID_AND_TYPE_AND_PERIOD_QUERY)
    fun getOperationsByAccountAndTypeAndPeriod(
        accountId: Int,
        operationType: OperationType,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<OperationDbExtended>>

    @Transaction
    @Query(GET_OPERATIONS_BY_TYPE_AND_PERIOD_QUERY)
    fun getOperationsByTypeAndPeriod(
        operationType: OperationType,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<OperationDbExtended>>

    @Transaction
    @Query("SELECT * FROM operationdb")
    fun getAll(): Flow<List<OperationDbExtended>>

    @Transaction
    @Query("SELECT * FROM operationdb WHERE id = :operationId")
    suspend fun getOperationById(operationId: Int): OperationDbExtended

    @Query(GET_GROUPED_CATEGORIES_BY_ACCOUNT_ID_QUERY)
    fun getGroupedCategoriesByAccountId(
        accountId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<GroupedCategoriesDb>>

    @Query(GET_ALL_GROUPED_CATEGORIES_QUERY)
    fun getAllGroupedCategories(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<GroupedCategoriesDb>>

    @Transaction
    @Query(GET_OPERATIONS_BY_CATEGORY_AND_ACCOUNT_QUERY)
    fun getOperationsByCategoryAndAccount(
        categoryId: Int,
        accountId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<OperationDbExtended>>

    @Transaction
    @Query(GET_OPERATIONS_BY_CATEGORY_QUERY)
    fun getOperationsByCategory(
        categoryId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<OperationDbExtended>>

    companion object {
        const val GET_OPERATIONS_BY_ACCOUNT_ID_AND_TYPE_AND_PERIOD_QUERY = """
            SELECT
	            operationdb.id,
                categoryid,
                subcategoryid,
                accountid,
                sum,
                date,
                comment
            FROM
	            operationdb
	            JOIN categorydb ON operationdb.categoryid = categorydb.id
            WHERE
                operationdb.accountid = :accountId
	            AND categorydb.type = :operationType
                AND date BETWEEN :startDate AND :endDate
        """

        const val GET_OPERATIONS_BY_TYPE_AND_PERIOD_QUERY = """
            SELECT
	            operationdb.id,
                categoryid,
                subcategoryid,
                accountid,
                sum,
                date,
                comment
            FROM
	            operationdb
	            JOIN categorydb ON operationdb.categoryid = categorydb.id
            WHERE
	            categorydb.type = :operationType
                AND date BETWEEN :startDate AND :endDate
        """

        const val GET_GROUPED_CATEGORIES_BY_ACCOUNT_ID_QUERY = """
            SELECT
                operationType,
                categoryName,
                sum(sum) AS totalSum,
                categoryId
            FROM
                (
                    SELECT
                        categorydb.type as operationType,
                        categorydb.name as categoryName,
                        operationdb.sum,
                        operationdb.categoryId
                    FROM
                        operationdb JOIN categorydb ON operationdb.categoryId = categoryDb.id
                    WHERE 
                        operationdb.date BETWEEN :startDate AND :endDate
                        AND operationDb.accountId = :accountId
                    UNION ALL
                    SELECT
                        CASE
                            WHEN transferdb.fromAccountId = :accountId THEN 'OUTCOME_TRANSFER'
                            ELSE 'INCOME_TRANSFER'
                        END AS operationType,
                        CASE
                            WHEN transferdb.fromAccountId = :accountId THEN 'Исходящие переводы'
                            ELSE 'Входящие переводы'
                        END AS categoryName,
                        transferdb.sum,
                        NULL AS categoryId
                    FROM transferdb
                    WHERE 
                        transferdb.date BETWEEN :startDate AND :endDate
                        AND (
                            transferdb.fromAccountId = :accountId
                            OR transferdb.toAccountId = :accountId
                        )
                )
            GROUP BY
                operationType,
                categoryName,
                categoryId
        """

        const val GET_ALL_GROUPED_CATEGORIES_QUERY = """
            SELECT
                operationType,
                categoryName,
                sum(sum) AS totalSum,
                categoryId
            FROM
                (
                    SELECT
                        categorydb.type as operationType,
                        categorydb.name as categoryName,
                        operationdb.sum,
                        operationdb.categoryId
                    FROM
                        operationdb JOIN categorydb ON operationdb.categoryId = categoryDb.id
                    WHERE operationdb.date BETWEEN :startDate AND :endDate
                    UNION ALL
                    SELECT
                        'TRANSFER' AS operationType,
                        'Переводы' AS categoryName,
                        transferdb.sum,
                        NULL AS categoryId
                    FROM transferdb
                    WHERE
                        transferdb.date BETWEEN :startDate AND :endDate
                )
            GROUP BY
                operationType,
                categoryName,
                categoryId
        """

        const val GET_OPERATIONS_BY_CATEGORY_AND_ACCOUNT_QUERY = """
            SELECT * FROM operationdb
            WHERE
                categoryId = :categoryId
                AND accountId = :accountId
                AND date BETWEEN :startDate AND :endDate
        """

        const val GET_OPERATIONS_BY_CATEGORY_QUERY = """
            SELECT * FROM operationdb
            WHERE
                categoryId = :categoryId
                AND date BETWEEN :startDate AND :endDate
        """
    }
}