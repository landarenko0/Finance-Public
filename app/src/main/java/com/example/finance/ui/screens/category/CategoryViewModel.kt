package com.example.finance.ui.screens.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.finance.domain.entities.Category
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.usecases.CategoryInteractor
import com.example.finance.ui.navigation.CategoryScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _event = Channel<CategoryEvent>()
    val event = _event.receiveAsFlow()

    val categoryId = savedStateHandle.toRoute<CategoryScreen>().categoryId

    init {
        viewModelScope.launch {
            if (categoryId != null) {
                categoryInteractor.getCategoryById(categoryId).also { category ->
                    _uiState.update {
                        it.copy(
                            selectedOperationType = category.type,
                            categoryName = category.name,
                            details = CategoryDetails.EditCategory(
                                selectedCategoryName = category.name
                            )
                        )
                    }
                }
            } else {
                val initialSelectedOperationType =
                    savedStateHandle.toRoute<CategoryScreen>().initialSelectedOperationType

                _uiState.update {
                    it.copy(
                        selectedOperationType = initialSelectedOperationType,
                        details = CategoryDetails.CreateCategory
                    )
                }
            }
        }
    }

    fun onUiEvent(uiEvent: CategoryUiEvent) {
        when (uiEvent) {
            CategoryUiEvent.OnConfirmCategoryNameCollisionDialog -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            showCategoryNameCollisionDialog = false,
                            categoryNameError = true
                        )
                    }

                    _event.send(CategoryEvent.RequestCategoryNameFocus)
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

            CategoryUiEvent.OnSaveButtonCLick -> saveCategory()

            is CategoryUiEvent.OnCategoryNameChanged -> {
                if (uiEvent.categoryName.length <= 50) {
                    _uiState.update {
                        it.copy(
                            categoryName = uiEvent.categoryName,
                            categoryNameError = false
                        )
                    }
                }
            }

            is CategoryUiEvent.OnOperationTypeChanged -> {
                _uiState.update { it.copy(selectedOperationType = uiEvent.operationType) }
            }

            CategoryUiEvent.OnBackIconClick -> {
                viewModelScope.launch { _event.send(CategoryEvent.CloseScreen) }
            }
        }
    }

    private fun saveCategory() {
        viewModelScope.launch {
            val categoryName = _uiState.value.categoryName.trim()
            val operationType = _uiState.value.selectedOperationType

            if (!validateInput(categoryName, operationType)) return@launch

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

            _event.send(CategoryEvent.CloseScreen)
        }
    }

    private suspend fun validateInput(
        categoryName: String,
        operationType: OperationType
    ): Boolean = viewModelScope.async {
        when {
            categoryName.isEmpty() || categoryName.isBlank() -> {
                _uiState.update { it.copy(categoryNameError = true) }
                _event.send(CategoryEvent.RequestCategoryNameFocus)
            }

            _uiState.value.details is CategoryDetails.CreateCategory && categoryInteractor.checkCategoryNameCollision(categoryName, operationType) -> {
                _uiState.update { it.copy(showCategoryNameCollisionDialog = true) }
            }

            _uiState.value.details is CategoryDetails.EditCategory && categoryInteractor.checkCategoryNameCollisionExcept(categoryName, operationType, categoryId!!) -> {
                _uiState.update { it.copy(showCategoryNameCollisionDialog = true) }
            }

            else -> return@async true
        }

        return@async false
    }.await()

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
                        showDeleteCategoryDialog = false
                    )
                )
            }

            _event.send(CategoryEvent.NavigateToCategoryListScreen)
        }
    }
}