package com.example.finance.ui.screens.categorywithsubcategories

sealed interface CategoryWithSubcategoriesUiEvent {

    data object OnComposition : CategoryWithSubcategoriesUiEvent

    data object OnDeleteIconClick : CategoryWithSubcategoriesUiEvent

    data object OnCloseIconClick : CategoryWithSubcategoriesUiEvent

    data object OnFloatingButtonClick : CategoryWithSubcategoriesUiEvent

    data class OnSubcategoryClick(val subcategoryId: Int) : CategoryWithSubcategoriesUiEvent

    data class OnLongSubcategoryClick(val subcategoryId: Int) : CategoryWithSubcategoriesUiEvent

    data class OnSaveButtonClick(val subcategoryName: String) : CategoryWithSubcategoriesUiEvent

    data object OnDialogDismiss : CategoryWithSubcategoriesUiEvent

    data object OnConfirmSubcategoryNameCollision : CategoryWithSubcategoriesUiEvent

    data object OnConfirmDeleteSubcategoriesDialog : CategoryWithSubcategoriesUiEvent
}