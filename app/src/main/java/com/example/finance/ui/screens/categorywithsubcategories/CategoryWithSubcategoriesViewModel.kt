package com.example.finance.ui.screens.categorywithsubcategories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.finance.domain.entities.Subcategory
import com.example.finance.domain.usecases.CategoryInteractor
import com.example.finance.domain.usecases.SubcategoryInteractor
import com.example.finance.ui.navigation.CategoryWithSubcategoriesScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryWithSubcategoriesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    categoryInteractor: CategoryInteractor,
    private val subcategoryInteractor: SubcategoryInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryWithSubcategoriesUiState())
    val uiState: StateFlow<CategoryWithSubcategoriesUiState> = _uiState.asStateFlow()

    private val categoryId = savedStateHandle.toRoute<CategoryWithSubcategoriesScreen>().categoryId

    init {
        categoryInteractor.getCategoryWithSubcategoriesById(categoryId)
            .filterNotNull()
            .onEach { categoryWithSubcategories ->
                _uiState.update {
                    it.copy(
                        category = categoryWithSubcategories.category,
                        subcategories = categoryWithSubcategories.subcategories
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onUiEvent(uiEvent: CategoryWithSubcategoriesUiEvent) {
        when (uiEvent) {
            CategoryWithSubcategoriesUiEvent.OnCloseIconClick -> {
                _uiState.update {
                    it.copy(
                        selectedSubcategories = emptyList(),
                        deleteSubcategoriesEnabled = false
                    )
                }
            }

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

            is CategoryWithSubcategoriesUiEvent.OnSubcategoryClick -> {
                onSubcategoryClick(uiEvent.subcategoryId)
            }

            is CategoryWithSubcategoriesUiEvent.OnLongSubcategoryClick -> {
                _uiState.update { it.copy(deleteSubcategoriesEnabled = true) }

                if (uiEvent.subcategoryId !in _uiState.value.selectedSubcategories) {
                    val selectedSubcategories = _uiState.value.selectedSubcategories.toMutableList()
                    selectedSubcategories += uiEvent.subcategoryId

                    _uiState.update {
                        it.copy(selectedSubcategories = selectedSubcategories.toList())
                    }
                }
            }

            is CategoryWithSubcategoriesUiEvent.OnSaveButtonClick -> {
                saveSubCategory(uiEvent.subcategoryName)
            }
        }
    }

    private fun saveSubCategory(subcategoryName: String) {
        viewModelScope.launch {
            when (val selectedSubcategory = _uiState.value.selectedSubcategory) {
                null -> {
                    if (subcategoryInteractor.checkSubcategoryNameCollision(subcategoryName, categoryId)) {
                        _uiState.update { it.copy(showSubcategoryNameCollisionDialog = true) }
                    } else {
                        subcategoryInteractor.addSubcategory(
                            Subcategory(
                                id = 0,
                                categoryId = categoryId,
                                name = subcategoryName
                            )
                        )

                        _uiState.update {
                            it.copy(
                                selectedSubcategory = null,
                                showSubcategoryDialog = false
                            )
                        }
                    }
                }

                else -> {
                    if (subcategoryInteractor.checkSubcategoryNameCollisionExcept(subcategoryName, categoryId, selectedSubcategory.id)) {
                        _uiState.update { it.copy(showSubcategoryNameCollisionDialog = true) }
                    } else {
                        subcategoryInteractor.updateSubcategory(
                            selectedSubcategory.copy(name = subcategoryName)
                        )

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

    private fun deleteSubcategories(subcategoriesIds: List<Int>) {
        viewModelScope.launch {
            subcategoryInteractor.deleteSubcategoriesByIds(subcategoriesIds)

            _uiState.update {
                it.copy(
                    selectedSubcategories = emptyList(),
                    deleteSubcategoriesEnabled = false,
                    showDeleteSubcategoriesDialog = false
                )
            }
        }
    }

    private fun onSubcategoryClick(subcategoryId: Int) {
        if (!_uiState.value.deleteSubcategoriesEnabled) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        selectedSubcategory = subcategoryInteractor.getSubcategoryById(subcategoryId),
                        showSubcategoryDialog = true
                    )
                }
            }
        } else {
            val selectedSubcategories = _uiState.value.selectedSubcategories.toMutableList()

            when {
                subcategoryId in _uiState.value.selectedSubcategories -> {
                    selectedSubcategories.remove(subcategoryId)
                    _uiState.update {
                        it.copy(selectedSubcategories = selectedSubcategories.toList())
                    }
                }

                else -> {
                    selectedSubcategories.add(subcategoryId)
                    _uiState.update {
                        it.copy(selectedSubcategories = selectedSubcategories.toList())
                    }
                }
            }
        }
    }
}