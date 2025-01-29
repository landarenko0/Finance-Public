package com.example.finance.ui.screens.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.finance.domain.entities.Category
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.usecases.CategoryInteractor
import com.example.finance.ui.navigation.AppScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryInteractor: CategoryInteractor
) : ViewModel() {

    private val _uiState: MutableStateFlow<CategoryUiState> = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    val categoryId = savedStateHandle.toRoute<AppScreens.CategoryScreen>().categoryId

    init {
        viewModelScope.launch {
            if (categoryId != null) {
                _uiState.value = _uiState.value.copy(details = CategoryDetails.EditCategory())

                categoryInteractor.getCategoryById(categoryId).also { category ->
                    _uiState.update {
                        it.copy(
                            selectedOperationType = category.type,
                            categoryName = category.name,
                            details = (it.details as CategoryDetails.EditCategory).copy(
                                selectedCategoryName = category.name
                            )
                        )
                    }
                }

                categoryInteractor
                    .getAllCategories()
                    .map { categories -> categories.filter { it.id != categoryId } }
                    .first()
                    .also { categories -> _uiState.update { it.copy(categories = categories) } }
            } else {
                val initialSelectedOperationType = savedStateHandle.toRoute<AppScreens.CategoryScreen>().initialSelectedOperationType

                _uiState.value = _uiState.value.copy(
                    selectedOperationType = initialSelectedOperationType,
                    details = CategoryDetails.CreateCategory
                )

                categoryInteractor
                    .getAllCategories()
                    .first()
                    .also { categories -> _uiState.update { it.copy(categories = categories) } }
            }
        }
    }

    fun onUiEvent(uiEvent: CategoryUiEvent) {
        when (uiEvent) {
            CategoryUiEvent.OnConfirmCategoryNameCollisionDialog -> {
                _uiState.update {
                    it.copy(
                        showCategoryNameCollisionDialog = false,
                        categoryNameError = true,
                        requestCategoryNameFocus = true
                    )
                }
            }

            CategoryUiEvent.OnConfirmDeleteCategoryDialog -> deleteCategory()

            CategoryUiEvent.OnDeleteIconClick -> {
                _uiState.update {
                    it.copy(
                        details = (it.details as CategoryDetails.EditCategory).copy(
                            showDeleteCategoryDialog = true
                        )
                    )
                }
            }

            CategoryUiEvent.OnDialogDismiss -> {
                _uiState.update {
                    it.copy(
                        details = (it.details as CategoryDetails.EditCategory).copy(
                            showDeleteCategoryDialog = false
                        )
                    )
                }
            }

            CategoryUiEvent.OnFocusRequested -> {
                _uiState.update { it.copy(requestCategoryNameFocus = false) }
            }

            CategoryUiEvent.OnSaveButtonCLick -> saveCategory()
        }
    }

    private fun saveCategory() {
        if (!validateCategory()) return

        val categoryName = _uiState.value.categoryName.trim()
        val operationType = _uiState.value.selectedOperationType

        viewModelScope.launch {
            when (_uiState.value.details) {
                CategoryDetails.CreateCategory -> {
                    categoryInteractor.addCategory(
                        Category(
                            id = 0,
                            name = categoryName,
                            type = operationType
                        )
                    )
                }

                is CategoryDetails.EditCategory -> {
                    categoryId?.let {
                        categoryInteractor.getCategoryById(categoryId).also { category ->
                            categoryInteractor.updateCategory(
                                category.copy(name = categoryName)
                            )
                        }
                    }
                }

                CategoryDetails.Initial -> {
                    throw RuntimeException("CategoryDetails must be CreateCategory or EditCategory")
                }
            }

            _uiState.update { it.copy(closeScreen = true) }
        }
    }

    private fun validateCategory(): Boolean {
        val categoryName = _uiState.value.categoryName.trim()
        val operationType = _uiState.value.selectedOperationType

        when {
            categoryName.isEmpty() || categoryName.isBlank() -> {
                _uiState.update {
                    it.copy(
                        categoryNameError = true,
                        requestCategoryNameFocus = true
                    )
                }
            }

            checkCategoryNameCollision(categoryName, operationType) -> {
                _uiState.update { it.copy(showCategoryNameCollisionDialog = true) }
            }

            else -> return true
        }

        return false
    }

    private fun checkCategoryNameCollision(
        categoryName: String,
        operationType: OperationType
    ): Boolean {
        return _uiState.value.categories
            .filter { it.type == operationType }
            .find { it.name == categoryName } != null
    }

    private fun deleteCategory() {
        viewModelScope.launch {
            categoryId?.let {
                categoryInteractor.getCategoryById(categoryId).also { category ->
                    categoryInteractor.deleteCategory(category)
                }
            }

            _uiState.update {
                it.copy(
                    details = (it.details as CategoryDetails.EditCategory).copy(
                        showDeleteCategoryDialog = false,
                        categoryDeleted = true
                    )
                )
            }
        }
    }

    fun updateCategoryName(categoryName: String) {
        if (categoryName.length <= 50) {
            _uiState.update {
                it.copy(
                    categoryName = categoryName,
                    categoryNameError = false
                )
            }
        }
    }

    fun updateOperationType(operationType: OperationType) {
        _uiState.update { it.copy(selectedOperationType = operationType) }
    }
}