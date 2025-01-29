package com.example.finance.ui.screens.categorylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.usecases.CategoryInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val categoryInteractor: CategoryInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryInteractor
                .getAllCategories()
                .map { it.reversed() }
                .collect { categories ->
                    _uiState.update {
                        it.copy(
                            expensesCategories = categories.filter { category ->
                                category.type == OperationType.EXPENSES
                            },
                            incomeCategories = categories.filter { category ->
                                category.type == OperationType.INCOME
                            }
                        )
                    }
                }
        }
    }

    fun updateOperationType(operationType: OperationType) {
        _uiState.update { it.copy(selectedOperationType = operationType) }
    }
}