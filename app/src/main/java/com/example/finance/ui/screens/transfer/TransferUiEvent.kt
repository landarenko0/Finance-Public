package com.example.finance.ui.screens.transfer

sealed interface TransferUiEvent {

    data class OnNewFromAccountSelected(val accountId: Int) : TransferUiEvent

    data class OnNewToAccountSelected(val accountId: Int) : TransferUiEvent

    data class OnNewDateSelected(val date: Long) : TransferUiEvent

    data object OnSaveButtonClick : TransferUiEvent

    data object OnDeleteIconClick : TransferUiEvent

    data object OnDismissDialog : TransferUiEvent

    data object OnDismissDeleteTransferDialog : TransferUiEvent

    data object OnFromAccountPickerClick : TransferUiEvent

    data object OnToAccountPickerClick : TransferUiEvent

    data object OnDatePickerClick : TransferUiEvent

    data object OnConfirmDeleteTransferDialog : TransferUiEvent

    data object OnFocusRequested : TransferUiEvent
}