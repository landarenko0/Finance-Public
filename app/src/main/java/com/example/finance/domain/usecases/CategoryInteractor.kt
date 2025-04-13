package com.example.finance.domain.usecases

import com.example.finance.domain.entities.Category
import com.example.finance.domain.entities.CategoryWithSubcategories
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class CategoryInteractor(
    val getCategoryById: GetObjectByIdUseCase<Category>,
    val addCategory: InsertUseCase<Category>,
    val updateCategory: UpdateUseCase<Category>,
    val deleteCategory: DeleteUseCase<Category>,
    val getCategoriesByType: GetCategoriesByTypeUseCase,
    val getCategoryWithSubcategoriesById: GetCategoryWithSubcategoriesByIdUseCase,
    val checkCategoryNameCollision: CheckCategoryNameCollisionUseCase,
    val checkCategoryNameCollisionExcept: CheckCategoryNameCollisionExceptUseCase
)

class GetCategoriesByTypeUseCase(private val repository: CategoryRepository) {

    operator fun invoke(type: OperationType): Flow<List<Category>> =
        repository.getCategoriesByType(type)
}

class GetCategoryWithSubcategoriesByIdUseCase(private val repository: CategoryRepository) {

    operator fun invoke(categoryId: Int): Flow<CategoryWithSubcategories?> =
        repository.getCategoryWithSubcategoriesById(categoryId)
}

class CheckCategoryNameCollisionUseCase(private val repository: CategoryRepository) {

    suspend operator fun invoke(categoryName: String, operationType: OperationType): Boolean {
        val categories = repository.getCategoriesByType(operationType).first()

        return withContext(Dispatchers.Default) {
            categories.find { it.name == categoryName } != null
        }
    }
}

class CheckCategoryNameCollisionExceptUseCase(private val repository: CategoryRepository) {

    suspend operator fun invoke(
        categoryName: String,
        operationType: OperationType,
        exceptCategoryId: Int
    ): Boolean {
        val categories = repository.getCategoriesByType(operationType).first()

        return withContext(Dispatchers.Default) {
            categories.find { it.name == categoryName && it.id != exceptCategoryId } != null
        }
    }
}