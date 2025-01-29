package com.example.finance.ui.screens.category

sealed interface CategoryUiEvent {

    data object OnSaveButtonCLick : CategoryUiEvent

    data object OnDeleteIconClick : CategoryUiEvent

    data object OnFocusRequested : CategoryUiEvent

    data object OnConfirmCategoryNameCollisionDialog : CategoryUiEvent

    data object OnConfirmDeleteCategoryDialog : CategoryUiEvent

    data object OnDialogDismiss : CategoryUiEvent
}