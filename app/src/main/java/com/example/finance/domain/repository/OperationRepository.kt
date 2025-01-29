package com.example.finance.domain.repository

import com.example.finance.domain.entities.GroupedCategories
import com.example.finance.domain.entities.Operation
import com.example.finance.domain.entities.Period
import kotlinx.coroutines.flow.Flow

interface OperationRepository : BaseRepository<Operation> {

    fun getGroupedCategoriesByAccountId(
        accountId: Int,
        period: Period
    ): Flow<List<GroupedCategories>>

    fun getAllGroupedCategories(period: Period): Flow<List<GroupedCategories>>

    fun getOperationsByCategoryId(
        categoryId: Int,
        accountId: Int,
        period: Period
    ): Flow<List<Operation>>

    suspend fun deleteOperationsByAccountId(accountId: Int)
}