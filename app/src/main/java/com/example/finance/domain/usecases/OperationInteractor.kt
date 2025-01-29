package com.example.finance.domain.usecases

import com.example.finance.domain.entities.GroupedCategories
import com.example.finance.domain.entities.Operation
import com.example.finance.domain.entities.Period
import com.example.finance.domain.repository.OperationRepository
import kotlinx.coroutines.flow.Flow

class OperationInteractor(
    val getAllOperations: GetAllUseCase<Operation>,
    val getOperationById: GetObjectByIdUseCase<Operation>,
    val addOperation: InsertUseCase<Operation>,
    val addAllOperations: InsertAllUseCase<Operation>,
    val updateOperation: UpdateUseCase<Operation>,
    val deleteOperation: DeleteUseCase<Operation>,
    val deleteOperationById: DeleteObjectByIdUseCase<Operation>,
    val getGroupedCategories: GetGroupedCategoriesUseCase,
    val getOperationsByCategoryId: GetOperationsByCategoryIdUseCase,
    val deleteOperationsByAccountId: DeleteOperationsByAccountIdUseCase,
    val getAllGroupedCategories: GetAllGroupedCategoriesUseCase
)

class GetGroupedCategoriesUseCase(private val repository: OperationRepository) {

    operator fun invoke(accountId: Int, period: Period): Flow<List<GroupedCategories>> =
        repository.getGroupedCategoriesByAccountId(accountId, period)
}

class GetAllGroupedCategoriesUseCase(private val repository: OperationRepository) {

    operator fun invoke(period: Period): Flow<List<GroupedCategories>> =
        repository.getAllGroupedCategories(period)
}

class GetOperationsByCategoryIdUseCase(private val repository: OperationRepository) {

    operator fun invoke(
        categoryId: Int,
        accountId: Int,
        period: Period
    ): Flow<List<Operation>> = repository.getOperationsByCategoryId(categoryId, accountId, period)
}

class DeleteOperationsByAccountIdUseCase(private val repository: OperationRepository) {

    suspend operator fun invoke(accountId: Int) = repository.deleteOperationsByAccountId(accountId)
}