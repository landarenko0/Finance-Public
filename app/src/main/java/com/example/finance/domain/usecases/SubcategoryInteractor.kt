package com.example.finance.domain.usecases

import com.example.finance.domain.entities.Subcategory
import com.example.finance.domain.repository.SubcategoryRepository

class SubcategoryInteractor(
    val getAllSubcategories: GetAllUseCase<Subcategory>,
    val getSubcategoryById: GetObjectByIdUseCase<Subcategory>,
    val addSubcategory: InsertUseCase<Subcategory>,
    val addAllSubcategories: InsertAllUseCase<Subcategory>,
    val updateSubcategory: UpdateUseCase<Subcategory>,
    val deleteSubcategory: DeleteUseCase<Subcategory>,
    val deleteSubcategoryById: DeleteObjectByIdUseCase<Subcategory>,
    val deleteSubcategoriesByIds: DeleteSubcategoriesByIdsUseCase
)

class DeleteSubcategoriesByIdsUseCase(private val repository: SubcategoryRepository) {

    suspend operator fun invoke(subcategoriesIds: List<Int>) =
        repository.deleteSubcategoriesByIds(subcategoriesIds)
}