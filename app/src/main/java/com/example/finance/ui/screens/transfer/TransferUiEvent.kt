package com.example.finance.ui.screens.transfer

sealed interface TransferUiEvent {

    data class OnFromAccountSelected(val accountId: Int) : TransferUiEvent

    data class OnToAccountSelected(val accountId: Int) : TransferUiEvent

    data class OnDateSelected(val date: Long) : TransferUiEvent

    data class OnTransferSumChanged(val sum: String) : TransferUiEvent

    data class OnCommentChanged(val comment: String) : TransferUiEvent

    data object OnSaveButtonClick : TransferUiEvent

    data object OnDeleteIconClick : TransferUiEvent

    data object OnDismissDialog : TransferUiEvent

    data object OnDismissDeleteTransferDialog : TransferUiEvent

    data object OnFromAccountPickerClick : TransferUiEvent

    data object OnToAccountPickerClick : TransferUiEvent

    data object OnDatePickerClick : TransferUiEvent

    data object OnConfirmDeleteTransferDialog : TransferUiEvent

    data object OnBackIconClick : TransferUiEvent
}