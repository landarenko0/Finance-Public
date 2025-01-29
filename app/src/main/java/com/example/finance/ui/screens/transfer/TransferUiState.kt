package com.example.finance.ui.screens.transfer

import com.example.finance.domain.entities.Account
import java.time.Instant

data class TransferUiState(
    val transferSum: String = "",
    val selectedFromAccount: Account? = null,
    val selectedToAccount: Account? = null,
    val selectedDate: Long = Instant.now().toEpochMilli(),
    val comment: String = "",
    val transferSumError: Boolean = false,
    val fromAccountIdError: Boolean = false,
    val toAccountIdError: Boolean = false,
    val requestTransferSumFocus: Boolean = false,
    val showSelectFromAccountDialog: Boolean = false,
    val showSelectToAccountDialog: Boolean = false,
    val showDatePickerDialog: Boolean = false,
    val showSelectedAccountsAreSameDialog: Boolean = false,
    val accounts: List<Account> = emptyList(),
    val closeScreen: Boolean = false,
    val details: TransferDetails = TransferDetails.Initial
)

sealed interface TransferDetails {

    data object Initial : TransferDetails

    data object CreateTransfer : TransferDetails

    data class EditTransfer(val showDeleteTransferDialog: Boolean = false) : TransferDetails
}