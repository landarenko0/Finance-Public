package com.example.finance.ui.screens.operation

import com.example.finance.domain.entities.Account
import com.example.finance.domain.entities.Category
import com.example.finance.domain.entities.Subcategory

data class OperationUiState(
    val operationSum: String = "",
    val sign: String = "",
    val selectedAccount: Account? = null,
    val selectedCategory: Category? = null,
    val selectedSubcategory: Subcategory? = null,
    val selectedDate: Long = System.currentTimeMillis(),
    val accounts: List<Account> = emptyList(),
    val incomeCategories: List<Category> = emptyList(),
    val expensesCategories: List<Category> = emptyList(),
    val subcategories: List<Subcategory> = emptyList(),
    val comment: String = "",
    val operationSumError: Boolean = false,
    val showSubcategoryPicker: Boolean = false,
    val showAccountPickerDialog: Boolean = false,
    val showCategoryPickerDialog: Boolean = false,
    val showSubcategoryPickerDialog: Boolean = false,
    val showDatePickerDialog: Boolean = false,
    val details: OperationDetails = OperationDetails.Initial
)

sealed interface OperationDetails {

    data object Initial : OperationDetails

    data object CreateOperation : OperationDetails

    data class EditOperation(val showDeleteOperationDialog: Boolean = false) : OperationDetails
}