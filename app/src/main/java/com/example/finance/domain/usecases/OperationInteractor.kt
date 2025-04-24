package com.example.finance.domain.usecases

import com.example.finance.domain.entities.GroupedCategories
import com.example.finance.domain.entities.Operation
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.Period
import com.example.finance.domain.repository.OperationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.YearMonth

class OperationInteractor(
    val getOperationById: GetObjectByIdUseCase<Operation>,
    val addOperation: InsertUseCase<Operation>,
    val updateOperation: UpdateUseCase<Operation>,
    val deleteOperation: DeleteUseCase<Operation>,
    val getGroupedCategories: GetGroupedCategoriesUseCase,
    val getOperationsByCategoryAndAccount: GetOperationsByCategoryAndAccountUseCase,
    val getOperationsByCategory: GetOperationsByCategoryUseCase,
    val getAllGroupedCategories: GetAllGroupedCategoriesUseCase,
    val getSumOfOperationsByMonthForTheYear: GetSumOfOperationsByMonthForTheYearUseCase
)

class GetGroupedCategoriesUseCase(private val repository: OperationRepository) {

    operator fun invoke(accountId: Int, period: Period): Flow<List<GroupedCategories>> =
        repository.getGroupedCategoriesByAccountAndPeriod(accountId, period)
}

class GetAllGroupedCategoriesUseCase(private val repository: OperationRepository) {

    operator fun invoke(period: Period): Flow<List<GroupedCategories>> =
        repository.getAllGroupedCategoriesByPeriod(period)
}

class GetOperationsByCategoryAndAccountUseCase(private val repository: OperationRepository) {

    operator fun invoke(
        categoryId: Int,
        accountId: Int,
        period: Period
    ): Flow<List<Operation>> =
        repository.getOperationsByCategoryAndAccountAndPeriod(categoryId, accountId, period)
}

class GetOperationsByCategoryUseCase(private val repository: OperationRepository) {

    operator fun invoke(
        categoryId: Int,
        period: Period
    ): Flow<List<Operation>> = repository.getOperationsByCategoryAndPeriod(categoryId, period)
}

class GetSumOfOperationsByMonthForTheYearUseCase(private val repository: OperationRepository) {

    // Output: { EXPENSES: { "2025-01": 1000.0 }, INCOMES: { "2025-01": 2000.0 } }
    operator fun invoke(accountId: Int): Flow<Map<OperationType, Map<YearMonth, Double>>> {
        val now = YearMonth.now()
        val last12Months = (0L..11L).map { now.minusMonths(it) }.reversed()

        val startDate = last12Months.first().atDay(1)
        val endDate = last12Months.last().atEndOfMonth()

        val period = Period(startDate, endDate)

        val expensesFlow = if (accountId != 0) {
            repository.getOperationsByAccountAndTypeAndPeriod(
                accountId = accountId,
                operationType = OperationType.EXPENSES,
                period = period
            )
        } else {
            repository.getOperationsByTypeAndPeriod(
                operationType = OperationType.EXPENSES,
                period = period
            )
        }.map { operations ->
            val grouped = operations
                .groupBy { YearMonth.from(it.date) }
                .mapValues { entry -> entry.value.sumOf { it.sum }.toDouble() }

            last12Months.associateWith { grouped[it] ?: 0.0 }
        }
            .flowOn(Dispatchers.Default)

        val incomesFlow = if (accountId != 0) {
            repository.getOperationsByAccountAndTypeAndPeriod(
                accountId = accountId,
                operationType = OperationType.INCOME,
                period = period
            )
        } else {
            repository.getOperationsByTypeAndPeriod(
                operationType = OperationType.INCOME,
                period = period
            )
        }.map { operations ->
            val grouped = operations
                .groupBy { YearMonth.from(it.date) }
                .mapValues { entry -> entry.value.sumOf { it.sum }.toDouble() }

            last12Months.associateWith { grouped[it] ?: 0.0 }
        }
            .flowOn(Dispatchers.Default)

        return expensesFlow.combine(incomesFlow) { expenses, incomes ->
            mapOf(
                OperationType.EXPENSES to expenses,
                OperationType.INCOME to incomes
            )
        }
    }
}