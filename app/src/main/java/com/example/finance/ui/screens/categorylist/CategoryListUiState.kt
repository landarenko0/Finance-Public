package com.example.finance.ui.screens.categorylist

import com.example.finance.domain.entities.Category
import com.example.finance.domain.entities.OperationType

data class CategoryListUiState(
    val selectedOperationType: OperationType = OperationType.EXPENSES,
    val expensesCategories: List<Category> = emptyList(),
    val incomeCategories: List<Category> = emptyList()
)
