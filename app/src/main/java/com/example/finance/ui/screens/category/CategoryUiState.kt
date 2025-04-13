package com.example.finance.ui.screens.category

import com.example.finance.domain.entities.OperationType

data class CategoryUiState(
    val selectedOperationType: OperationType = OperationType.EXPENSES,
    val categoryName: String = "",
    val categoryNameError: Boolean = false,
    val showCategoryNameCollisionDialog: Boolean = false,
    val details: CategoryDetails = CategoryDetails.Initial
)

sealed interface CategoryDetails {

    data object Initial : CategoryDetails

    data object CreateCategory : CategoryDetails

    data class EditCategory(
        val selectedCategoryName: String = "",
        val showDeleteCategoryDialog: Boolean = false
    ) : CategoryDetails
}