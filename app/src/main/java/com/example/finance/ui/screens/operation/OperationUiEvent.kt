package com.example.finance.ui.screens.operation

sealed interface OperationUiEvent {

    data object OnDeleteOperationIconClick : OperationUiEvent

    data object OnAccountPickerClick : OperationUiEvent

    data object OnCategoryPickerClick : OperationUiEvent

    data object OnSubcategoryPickerClick : OperationUiEvent

    data object OnDatePickerClick : OperationUiEvent

    data object OnSaveButtonClick : OperationUiEvent

    data class OnNewAccountSelected(val accountId: Int) : OperationUiEvent

    data class OnNewCategorySelected(val categoryId: Int) : OperationUiEvent

    data class OnNewSubcategorySelected(val subcategoryId: Int) : OperationUiEvent

    data class OnNewDateSelected(val date: Long) : OperationUiEvent

    data object OnDialogDismiss : OperationUiEvent

    data object OnDeleteOperationDialogDismiss : OperationUiEvent

    data object OnDeleteOperationConfirmed : OperationUiEvent

    data class OnOperationSumChanged(val operationSum: String) : OperationUiEvent

    data class OnCommentChanged(val comment: String) : OperationUiEvent

    data object OnBackIconClick : OperationUiEvent
}