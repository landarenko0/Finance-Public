package com.example.finance.ui.screens.categorylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.usecases.CategoryInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    categoryInteractor: CategoryInteractor
) : ViewModel() {

    private val selectedOperationType = MutableStateFlow(OperationType.EXPENSES)

    private val expensesCategories = categoryInteractor.getCategoriesByType(OperationType.EXPENSES)
    private val incomeCategories = categoryInteractor.getCategoriesByType(OperationType.INCOME)

    val uiState = combine(
        selectedOperationType,
        expensesCategories,
        incomeCategories
    ) { operationType, expenses, income ->
        CategoryListUiState(
            selectedOperationType = operationType,
            expensesCategories = expenses,
            incomeCategories = income
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CategoryListUiState()
    )

    fun updateOperationType(operationType: OperationType) {
        selectedOperationType.update { operationType }
    }
}