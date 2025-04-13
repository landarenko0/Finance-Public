package com.example.finance.ui.screens.categorywithsubcategories

import com.example.finance.domain.entities.Category
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.Subcategory

data class CategoryWithSubcategoriesUiState(
    val category: Category = Category(
        id = 0,
        name = "",
        type = OperationType.EXPENSES
    ),
    val subcategories: List<Subcategory> = emptyList(),
    val deleteSubcategoriesEnabled: Boolean = false,
    val selectedSubcategories: List<Int> = emptyList(),
    val selectedSubcategory: Subcategory? = null,
    val showSubcategoryDialog: Boolean = false,
    val showSubcategoryNameCollisionDialog: Boolean = false,
    val showDeleteSubcategoriesDialog: Boolean = false
)