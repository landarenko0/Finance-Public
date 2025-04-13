package com.example.finance.ui.screens.operation

sealed interface OperationEvent {

    data object CloseScreen : OperationEvent

    data object RequestOperationSumFocus : OperationEvent
}