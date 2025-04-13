package com.example.finance.ui.screens.transfer

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.finance.domain.entities.Transfer
import com.example.finance.domain.usecases.AccountInteractor
import com.example.finance.domain.usecases.TransferInteractor
import com.example.finance.ui.navigation.TransferScreen
import com.example.finance.utils.toLocalDate
import com.example.finance.utils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transferInteractor: TransferInteractor,
    private val accountInteractor: AccountInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    private val _event = Channel<TransferEvent>()
    val event = _event.receiveAsFlow()

    private val transferId = savedStateHandle.toRoute<TransferScreen>().transferId

    init {
        viewModelScope.launch {
            val accounts = accountInteractor.getAllAccounts().first()

            if (transferId != null) {
                transferInteractor.getTransferById(transferId).also { transfer ->
                    _uiState.update {
                        it.copy(
                            accounts = accounts,
                            transferSum = transfer.sum.toString(),
                            selectedFromAccount = transfer.fromAccount,
                            selectedToAccount = transfer.toAccount,
                            selectedDate = transfer.date.toMillis(),
                            comment = transfer.comment,
                            details = TransferDetails.EditTransfer()
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        accounts = accounts,
                        details = TransferDetails.CreateTransfer
                    )
                }
            }
        }
    }

    fun onUiEvent(uiEvent: TransferUiEvent) {
        when (uiEvent) {
            TransferUiEvent.OnConfirmDeleteTransferDialog -> deleteTransfer()

            TransferUiEvent.OnDatePickerClick -> {
                _uiState.update { it.copy(showDatePickerDialog = true) }
            }

            TransferUiEvent.OnDeleteIconClick -> {
                _uiState.update {
                    it.copy(
                        details = (it.details as TransferDetails.EditTransfer).copy(
                            showDeleteTransferDialog = true
                        )
                    )
                }
            }

            TransferUiEvent.OnDismissDialog -> {
                _uiState.update {
                    it.copy(
                        showDatePickerDialog = false,
                        showSelectFromAccountDialog = false,
                        showSelectedAccountsAreSameDialog = false,
                        showSelectToAccountDialog = false
                    )
                }
            }

            TransferUiEvent.OnDismissDeleteTransferDialog -> {
                _uiState.update {
                    it.copy(
                        details = (it.details as TransferDetails.EditTransfer).copy(
                            showDeleteTransferDialog = false
                        )
                    )
                }
            }

            TransferUiEvent.OnFromAccountPickerClick -> {
                _uiState.update { it.copy(showSelectFromAccountDialog = true) }
            }

            TransferUiEvent.OnToAccountPickerClick -> {
                _uiState.update { it.copy(showSelectToAccountDialog = true) }
            }

            is TransferUiEvent.OnDateSelected -> {
                _uiState.update {
                    it.copy(
                        selectedDate = uiEvent.date,
                        showDatePickerDialog = false
                    )
                }
            }

            is TransferUiEvent.OnFromAccountSelected -> updateFromAccount(uiEvent.accountId)

            is TransferUiEvent.OnToAccountSelected -> updateToAccount(uiEvent.accountId)

            TransferUiEvent.OnSaveButtonClick -> saveTransfer()

            is TransferUiEvent.OnCommentChanged -> {
                if (uiEvent.comment.length <= 100) {
                    _uiState.update { it.copy(comment = uiEvent.comment) }
                }
            }

            is TransferUiEvent.OnTransferSumChanged -> {
                if (uiEvent.sum.isDigitsOnly() && uiEvent.sum.length <= 12) {
                    _uiState.update {
                        it.copy(
                            transferSum = uiEvent.sum,
                            transferSumError = false
                        )
                    }
                }
            }

            TransferUiEvent.OnBackIconClick -> {
                viewModelScope.launch { _event.send(TransferEvent.CloseScreen) }
            }
        }
    }

    private fun saveTransfer() {
        if (!validateInput()) return

        viewModelScope.launch {
            val transferSum = _uiState.value.transferSum.toLong()
            val selectedFromAccount = _uiState.value.selectedFromAccount!!
            val selectedToAccount = _uiState.value.selectedToAccount!!
            val selectedDate = _uiState.value.selectedDate.toLocalDate()
            val comment = _uiState.value.comment.trim()

            when (_uiState.value.details) {
                is TransferDetails.CreateTransfer -> {
                    transferInteractor.addTransfer(
                        Transfer(
                            id = 0,
                            fromAccount = selectedFromAccount,
                            toAccount = selectedToAccount,
                            sum = transferSum,
                            date = selectedDate,
                            comment = comment
                        )
                    )
                }

                is TransferDetails.EditTransfer -> {
                    transferId?.let {
                        transferInteractor.getTransferById(transferId).also { oldTransfer ->
                            transferInteractor.updateTransfer(
                                oldTransfer.copy(
                                    fromAccount = selectedFromAccount,
                                    toAccount = selectedToAccount,
                                    sum = transferSum,
                                    date = selectedDate,
                                    comment = comment
                                )
                            )

                            accountInteractor.transferMoneyFromOneAccountToAnother(
                                oldTransfer.toAccount.id,
                                oldTransfer.fromAccount.id,
                                oldTransfer.sum
                            )
                        }
                    }
                }

                TransferDetails.Initial -> {}
            }

            accountInteractor.transferMoneyFromOneAccountToAnother(
                selectedFromAccount.id,
                selectedToAccount.id,
                transferSum
            )

            _event.send(TransferEvent.CloseScreen)
        }
    }

    private fun validateInput(): Boolean {
        val transferSum = _uiState.value.transferSum
        val selectedFromAccount = _uiState.value.selectedFromAccount
        val selectedToAccount = _uiState.value.selectedToAccount

        when {
            transferSum.isEmpty() || transferSum.toLongOrNull() == null || transferSum.toLongOrNull() == 0L -> {
                _uiState.update { it.copy(transferSumError = true) }
                viewModelScope.launch { _event.send(TransferEvent.RequestTransferSumFocus) }
            }

            selectedFromAccount == null -> {
                _uiState.update {
                    it.copy(
                        fromAccountIdError = true,
                        showSelectFromAccountDialog = true
                    )
                }
            }

            selectedToAccount == null -> {
                _uiState.update {
                    it.copy(
                        toAccountIdError = true,
                        showSelectToAccountDialog = true
                    )
                }
            }

            selectedFromAccount.id == selectedToAccount.id -> {
                _uiState.update {
                    it.copy(
                        showSelectedAccountsAreSameDialog = true,
                        fromAccountIdError = true,
                        toAccountIdError = true
                    )
                }
            }

            else -> return true
        }

        return false
    }

    private fun deleteTransfer() {
        viewModelScope.launch {
            transferId?.let {
                transferInteractor.getTransferById(transferId).also { oldTransfer ->
                    transferInteractor.deleteTransfer(oldTransfer)

                    accountInteractor.transferMoneyFromOneAccountToAnother(
                        oldTransfer.toAccount.id,
                        oldTransfer.fromAccount.id,
                        oldTransfer.sum
                    )
                }
            }

            _uiState.update {
                it.copy(
                    details = (it.details as TransferDetails.EditTransfer).copy(
                        showDeleteTransferDialog = false
                    )
                )
            }

            _event.send(TransferEvent.CloseScreen)
        }
    }

    private fun updateFromAccount(selectedAccountId: Int) {
        if (_uiState.value.selectedFromAccount?.id != selectedAccountId) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        selectedFromAccount = accountInteractor.getAccountById(selectedAccountId),
                        showSelectFromAccountDialog = false,
                        fromAccountIdError = false,
                        toAccountIdError = false
                    )
                }
            }
        } else {
            _uiState.update { it.copy(showSelectFromAccountDialog = false) }
        }
    }

    private fun updateToAccount(selectedAccountId: Int) {
        if (_uiState.value.selectedToAccount?.id != selectedAccountId) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        selectedToAccount = accountInteractor.getAccountById(selectedAccountId),
                        showSelectToAccountDialog = false,
                        fromAccountIdError = false,
                        toAccountIdError = false
                    )
                }
            }
        } else {
            _uiState.update { it.copy(showSelectToAccountDialog = false) }
        }
    }
}