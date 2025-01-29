package com.example.finance.domain.usecases

import com.example.finance.domain.entities.Category
import com.example.finance.domain.entities.CategoryWithSubcategories
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow


class CategoryInteractor(
    val getAllCategories: GetAllUseCase<Category>,
    val getCategoryById: GetObjectByIdUseCase<Category>,
    val addCategory: InsertUseCase<Category>,
    val addAllCategories: InsertAllUseCase<Category>,
    val updateCategory: UpdateUseCase<Category>,
    val deleteCategory: DeleteUseCase<Category>,
    val deleteCategoryById: DeleteObjectByIdUseCase<Category>,
    val getCategoriesByType: GetCategoriesByTypeUseCase,
    val getCategoryWithSubcategoriesById: GetCategoryWithSubcategoriesByIdUseCase
)

class GetCategoriesByTypeUseCase(private val repository: CategoryRepository) {

    operator fun invoke(type: OperationType): Flow<List<Category>> =
        repository.getCategoriesByType(type)
}

class GetCategoryWithSubcategoriesByIdUseCase(private val repository: CategoryRepository) {

    suspend operator fun invoke(categoryId: Int): CategoryWithSubcategories =
        repository.getCategoryWithSubcategoriesById(categoryId)
}