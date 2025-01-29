package com.example.finance.ui.screens.transfer

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.finance.domain.entities.Transfer
import com.example.finance.domain.usecases.AccountInteractor
import com.example.finance.domain.usecases.TransferInteractor
import com.example.finance.ui.navigation.AppScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transferInteractor: TransferInteractor,
    private val accountInteractor: AccountInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    private val transferId = savedStateHandle.toRoute<AppScreens.TransferScreen>().transferId

    init {
        viewModelScope.launch {
            if (transferId != null) {
                _uiState.value = _uiState.value.copy(details = TransferDetails.EditTransfer())

                transferInteractor.getTransferById(transferId).also { transfer ->
                    _uiState.update {
                        it.copy(
                            transferSum = transfer.sum.toString(),
                            selectedFromAccount = transfer.fromAccount,
                            selectedToAccount = transfer.toAccount,
                            selectedDate = transfer.date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                            comment = transfer.comment
                        )
                    }
                }
            } else {
                _uiState.value = _uiState.value.copy(details = TransferDetails.CreateTransfer)
            }

            accountInteractor
                .getAllAccounts()
                .map { it.reversed() }
                .first()
                .also { accounts -> _uiState.update { it.copy(accounts = accounts) } }
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
                        details = (it.details as TransferDetails.EditTransfer).copy(showDeleteTransferDialog = true)
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
                        details = (it.details as TransferDetails.EditTransfer).copy(showDeleteTransferDialog = false)
                    )
                }
            }

            TransferUiEvent.OnFocusRequested -> {
                _uiState.update { it.copy(requestTransferSumFocus = false) }
            }

            TransferUiEvent.OnFromAccountPickerClick -> {
                _uiState.update { it.copy(showSelectFromAccountDialog = true) }
            }

            TransferUiEvent.OnToAccountPickerClick -> {
                _uiState.update { it.copy(showSelectToAccountDialog = true) }
            }

            is TransferUiEvent.OnNewDateSelected -> {
                _uiState.update {
                    it.copy(
                        selectedDate = uiEvent.date,
                        showDatePickerDialog = false
                    )
                }
            }

            is TransferUiEvent.OnNewFromAccountSelected -> updateFromAccount(uiEvent.accountId)

            is TransferUiEvent.OnNewToAccountSelected -> updateToAccount(uiEvent.accountId)

            TransferUiEvent.OnSaveButtonClick -> saveTransfer()
        }
    }

    private fun saveTransfer() {
        if (!validateTransfer()) return

        viewModelScope.launch {
            val transferSum = _uiState.value.transferSum.toLong()
            val selectedFromAccount = _uiState.value.selectedFromAccount!!
            val selectedToAccount = _uiState.value.selectedToAccount!!
            val selectedDate = _uiState.value.selectedDate
            val comment = _uiState.value.comment.trim()

            when (_uiState.value.details) {
                is TransferDetails.CreateTransfer -> {
                    transferInteractor.addTransfer(
                        Transfer(
                            id = 0,
                            fromAccount = selectedFromAccount,
                            toAccount = selectedToAccount,
                            sum = transferSum,
                            date = Instant.ofEpochMilli(selectedDate).atZone(ZoneId.systemDefault()).toLocalDate(),
                            comment = comment
                        )
                    )

                    makeTransaction(selectedFromAccount.id, selectedToAccount.id, transferSum)
                }

                is TransferDetails.EditTransfer -> {
                    transferId?.let {
                        val oldTransfer = transferInteractor.getTransferById(transferId)

                        // revert old transaction
                        makeTransaction(
                            oldTransfer.toAccount.id,
                            oldTransfer.fromAccount.id,
                            oldTransfer.sum
                        )

                        transferInteractor.addTransfer(
                            oldTransfer.copy(
                                fromAccount = selectedFromAccount,
                                toAccount = selectedToAccount,
                                sum = transferSum,
                                date = Instant.ofEpochMilli(selectedDate).atZone(ZoneId.systemDefault()).toLocalDate(),
                                comment = comment
                            )
                        )

                        makeTransaction(selectedFromAccount.id, selectedToAccount.id, transferSum)
                    }
                }

                TransferDetails.Initial -> {}
            }

            _uiState.update { it.copy(closeScreen = true) }
        }
    }

    private fun validateTransfer(): Boolean {
        val transferSum = _uiState.value.transferSum
        val selectedFromAccount = _uiState.value.selectedFromAccount
        val selectedToAccount = _uiState.value.selectedToAccount

        when {
            transferSum.isEmpty() || transferSum.toLongOrNull() == null || transferSum.toLongOrNull() == 0L -> {
                _uiState.update {
                    it.copy(
                        transferSumError = true,
                        requestTransferSumFocus = true
                    )
                }
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
                val oldTransfer = transferInteractor.getTransferById(transferId)

                transferInteractor.deleteTransfer(oldTransfer)

                // revert old transaction
                makeTransaction(oldTransfer.toAccount.id, oldTransfer.fromAccount.id, oldTransfer.sum)

                _uiState.update { it.copy(closeScreen = true) }
            }
        }
    }

    private suspend fun makeTransaction(
        fromAccountId: Int,
        toAccountId: Int,
        sum: Long
    ) {
        accountInteractor.getAccountById(fromAccountId).also { fromAccount ->
            accountInteractor.updateAccount(
                fromAccount.copy(sum = fromAccount.sum - sum)
            )
        }

        accountInteractor.getAccountById(toAccountId).also { toAccount ->
            accountInteractor.updateAccount(
                toAccount.copy(sum = toAccount.sum + sum)
            )
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

    fun onTransferSumChanged(transferSum: String) {
        if (transferSum.isDigitsOnly() && transferSum.length <= 12) {
            _uiState.update {
                it.copy(
                    transferSum = transferSum,
                    transferSumError = false
                )
            }
        }
    }

    fun onCommentChanged(comment: String) {
        if (comment.length <= 100) _uiState.update { it.copy(comment = comment) }
    }
}