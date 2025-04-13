package com.example.finance.domain.usecases

import com.example.finance.domain.entities.GroupedCategories
import com.example.finance.domain.entities.Operation
import com.example.finance.domain.entities.Period
import com.example.finance.domain.repository.OperationRepository
import kotlinx.coroutines.flow.Flow

class OperationInteractor(
    val getOperationById: GetObjectByIdUseCase<Operation>,
    val addOperation: InsertUseCase<Operation>,
    val updateOperation: UpdateUseCase<Operation>,
    val deleteOperation: DeleteUseCase<Operation>,
    val getGroupedCategories: GetGroupedCategoriesUseCase,
    val getOperationsByCategoryAndAccount: GetOperationsByCategoryAndAccountUseCase,
    val getOperationsByCategory: GetOperationsByCategoryUseCase,
    val getAllGroupedCategories: GetAllGroupedCategoriesUseCase
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