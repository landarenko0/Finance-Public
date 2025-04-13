package com.example.finance.ui.screens.category

import com.example.finance.domain.entities.OperationType

sealed interface CategoryUiEvent {

    data class OnCategoryNameChanged(val categoryName: String) : CategoryUiEvent

    data class OnOperationTypeChanged(val operationType: OperationType) : CategoryUiEvent

    data object OnBackIconClick : CategoryUiEvent

    data object OnSaveButtonCLick : CategoryUiEvent

    data object OnDeleteIconClick : CategoryUiEvent

    data object OnConfirmCategoryNameCollisionDialog : CategoryUiEvent

    data object OnConfirmDeleteCategoryDialog : CategoryUiEvent

    data object OnDialogDismiss : CategoryUiEvent
}