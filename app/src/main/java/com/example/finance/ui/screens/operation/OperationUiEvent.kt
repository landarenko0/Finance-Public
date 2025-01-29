package com.example.finance.ui.screens.operation

sealed interface OperationUiEvent {

    data object OnDeleteOperationIconClick : OperationUiEvent

    data object OnAccountPickerClick : OperationUiEvent

    data object OnCategoryPickerClick : OperationUiEvent

    data object OnSubcategoryPickerClick : OperationUiEvent

    data object OnDatePickerClick : OperationUiEvent

    data object OnSaveButtonClick : OperationUiEvent

    data class OnNewAccountSelected(val newAccountId: Int) : OperationUiEvent

    data class OnNewCategorySelected(val newCategoryId: Int) : OperationUiEvent

    data class OnNewSubcategorySelected(val newSubcategoryId: Int) : OperationUiEvent

    data class OnNewDateSelected(val newDate: Long) : OperationUiEvent

    data object OnDialogDismiss : OperationUiEvent

    data object OnDeleteOperationDialogDismiss : OperationUiEvent

    data object OnFocusRequested : OperationUiEvent

    data object OnDeleteOperationConfirmed : OperationUiEvent
}