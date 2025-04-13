package com.example.finance.ui.screens.transfer

sealed interface TransferEvent {

    data object CloseScreen : TransferEvent

    data object RequestTransferSumFocus : TransferEvent
}