package com.example.finance.domain.repository

import com.example.finance.domain.entities.GroupedCategories
import com.example.finance.domain.entities.Operation
import com.example.finance.domain.entities.Period
import kotlinx.coroutines.flow.Flow

interface OperationRepository : BaseRepository<Operation> {

    fun getGroupedCategoriesByAccountAndPeriod(
        accountId: Int,
        period: Period
    ): Flow<List<GroupedCategories>>

    fun getAllGroupedCategoriesByPeriod(period: Period): Flow<List<GroupedCategories>>

    fun getOperationsByCategoryAndAccountAndPeriod(
        categoryId: Int,
        accountId: Int,
        period: Period
    ): Flow<List<Operation>>

    fun getOperationsByCategoryAndPeriod(
        categoryId: Int,
        period: Period
    ): Flow<List<Operation>>
}