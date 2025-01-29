package com.example.finance.ui.screens.categorywithsubcategories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.finance.domain.entities.Subcategory
import com.example.finance.domain.usecases.CategoryInteractor
import com.example.finance.domain.usecases.SubcategoryInteractor
import com.example.finance.ui.navigation.AppScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryWithSubcategoriesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryInteractor: CategoryInteractor,
    private val subcategoryInteractor: SubcategoryInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryWithSubcategoriesUiState())
    val uiState: StateFlow<CategoryWithSubcategoriesUiState> = _uiState.asStateFlow()

    private val categoryId = savedStateHandle.toRoute<AppScreens.CategoryWithSubcategoriesScreen>().categoryId

    fun onUiEvent(uiEvent: CategoryWithSubcategoriesUiEvent) {
        when (uiEvent) {
            CategoryWithSubcategoriesUiEvent.OnCloseIconClick -> {
                _uiState.update { it.copy(deleteSubcategoriesEnabled = false) }
                _uiState.value.selectedSubcategories.clear()
            }

            CategoryWithSubcategoriesUiEvent.OnComposition -> updateCategory()

            CategoryWithSubcategoriesUiEvent.OnConfirmDeleteSubcategoriesDialog -> {
                deleteSubcategories(_uiState.value.selectedSubcategories.toList())
            }

            CategoryWithSubcategoriesUiEvent.OnConfirmSubcategoryNameCollision -> {
                _uiState.update { it.copy(showSubcategoryNameCollisionDialog = false) }
            }

            CategoryWithSubcategoriesUiEvent.OnDeleteIconClick -> {
                _uiState.update { it.copy(showDeleteSubcategoriesDialog = true) }
            }

            CategoryWithSubcategoriesUiEvent.OnDialogDismiss -> {
                _uiState.update {
                    it.copy(
                        showSubcategoryDialog = false,
                        showDeleteSubcategoriesDialog = false,
                        selectedSubcategory = null
                    )
                }
            }

            CategoryWithSubcategoriesUiEvent.OnFloatingButtonClick -> {
                _uiState.update { it.copy(showSubcategoryDialog = true) }
            }

            is CategoryWithSubcategoriesUiEvent.OnSubcategoryClick -> onSubcategoryClick(uiEvent.subcategoryId)

            is CategoryWithSubcategoriesUiEvent.OnLongSubcategoryClick -> {
                _uiState.update { it.copy(deleteSubcategoriesEnabled = true) }

                if (uiEvent.subcategoryId !in _uiState.value.selectedSubcategories) {
                    _uiState.value.selectedSubcategories.add(uiEvent.subcategoryId)
                }
            }

            is CategoryWithSubcategoriesUiEvent.OnSaveButtonClick -> saveSubCategory(uiEvent.subcategoryName)
        }
    }

    private fun saveSubCategory(subcategoryName: String) {
        viewModelScope.launch {
            when (val selectedSubcategory = _uiState.value.selectedSubcategory) {
                null -> {
                    if (checkSubcategoryNameCollision(subcategoryName, 0)) {
                        _uiState.update { it.copy(showSubcategoryNameCollisionDialog = true) }
                    } else {
                        subcategoryInteractor.addSubcategory(
                            Subcategory(
                                id = 0,
                                categoryId = _uiState.value.category.id,
                                name = subcategoryName
                            )
                        )

                        updateCategory()

                        _uiState.update {
                            it.copy(
                                selectedSubcategory = null,
                                showSubcategoryDialog = false
                            )
                        }
                    }
                }

                else -> {
                    if (checkSubcategoryNameCollision(subcategoryName, selectedSubcategory.id)) {
                        _uiState.update { it.copy(showSubcategoryNameCollisionDialog = true) }
                    } else {
                        subcategoryInteractor.updateSubcategory(
                            selectedSubcategory.copy(name = subcategoryName)
                        )

                        updateCategory()

                        _uiState.update {
                            it.copy(
                                selectedSubcategory = null,
                                showSubcategoryDialog = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkSubcategoryNameCollision(
        subcategoryName: String,
        exceptSubcategoryId: Int
    ): Boolean {
        return _uiState.value.subcategories.find { it.name == subcategoryName && it.id != exceptSubcategoryId } != null
    }

    private fun updateCategory() {
        viewModelScope.launch {
            val categoryWithSubcategories = categoryInteractor.getCategoryWithSubcategoriesById(categoryId)

            _uiState.update {
                it.copy(
                    category = categoryWithSubcategories.category,
                    subcategories = categoryWithSubcategories.subcategories
                )
            }
        }
    }

    private fun deleteSubcategories(subcategoriesIds: List<Int>) {
        viewModelScope.launch {
            subcategoryInteractor.deleteSubcategoriesByIds(subcategoriesIds)
            updateCategory()

            _uiState.update {
                it.copy(
                    deleteSubcategoriesEnabled = false,
                    showDeleteSubcategoriesDialog = false
                )
            }

            _uiState.value.selectedSubcategories.clear()
        }
    }

    private fun onSubcategoryClick(subcategoryId: Int) {
        if (!_uiState.value.deleteSubcategoriesEnabled) {
            _uiState.update {
                it.copy(
                    selectedSubcategory = _uiState.value.subcategories.find { subcategory ->
                        subcategory.id == subcategoryId
                    },
                    showSubcategoryDialog = true
                )
            }
        } else {
            when {
                subcategoryId in _uiState.value.selectedSubcategories -> {
                    _uiState.value.selectedSubcategories.remove(subcategoryId)
                }

                else -> _uiState.value.selectedSubcategories.add(subcategoryId)
            }
        }
    }
}