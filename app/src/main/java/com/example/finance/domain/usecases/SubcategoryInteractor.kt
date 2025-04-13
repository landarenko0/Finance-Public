package com.example.finance.domain.usecases

import com.example.finance.domain.entities.Subcategory
import com.example.finance.domain.repository.SubcategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubcategoryInteractor(
    val getSubcategoryById: GetObjectByIdUseCase<Subcategory>,
    val addSubcategory: InsertUseCase<Subcategory>,
    val updateSubcategory: UpdateUseCase<Subcategory>,
    val deleteSubcategoriesByIds: DeleteSubcategoriesByIdsUseCase,
    val checkSubcategoryNameCollision: CheckSubcategoryNameCollisionUseCase,
    val checkSubcategoryNameCollisionExcept: CheckSubcategoryNameCollisionExceptUseCase,
    val getCategorySubcategories: GetCategorySubcategoriesUseCase
)

class DeleteSubcategoriesByIdsUseCase(private val repository: SubcategoryRepository) {

    suspend operator fun invoke(subcategoriesIds: List<Int>) =
        repository.deleteSubcategoriesByIds(subcategoriesIds)
}

class CheckSubcategoryNameCollisionUseCase(private val repository: SubcategoryRepository) {

    suspend operator fun invoke(subcategoryName: String, categoryId: Int) : Boolean {
        val subcategories = repository.getCategorySubcategories(categoryId)

        return withContext(Dispatchers.Default) {
            subcategories.find { it.name == subcategoryName } != null
        }
    }
}

class CheckSubcategoryNameCollisionExceptUseCase(private val repository: SubcategoryRepository) {

    suspend operator fun invoke(
        subcategoryName: String,
        categoryId: Int,
        exceptSubcategoryId: Int
    ): Boolean {
        val subcategories = repository.getCategorySubcategories(categoryId)

        return withContext(Dispatchers.Default) {
            subcategories.find { it.name == subcategoryName && it.id != exceptSubcategoryId } != null
        }
    }
}

class GetCategorySubcategoriesUseCase(private val repository: SubcategoryRepository) {

    suspend operator fun invoke(categoryId: Int): List<Subcategory> =
        repository.getCategorySubcategories(categoryId)
}