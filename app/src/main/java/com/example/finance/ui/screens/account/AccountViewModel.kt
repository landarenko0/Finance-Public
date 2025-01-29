package com.example.finance.ui.screens.account

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.finance.domain.entities.Account
import com.example.finance.domain.usecases.AccountInteractor
import com.example.finance.ui.navigation.AppScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val accountInteractor: AccountInteractor
) : ViewModel() {

    private val _uiState: MutableStateFlow<AccountUiState> = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    val accountId = savedStateHandle.toRoute<AppScreens.AccountScreen>().accountId

    init {
        viewModelScope.launch {
            if (accountId != null) {
                _uiState.value = _uiState.value.copy(details = AccountDetails.EditAccount())

                accountInteractor.getAccountById(accountId).also { account ->
                    _uiState.update {
                        it.copy(
                            accountSum = account.sum.toString(),
                            accountName = account.name,
                            details = (it.details as AccountDetails.EditAccount).copy(
                                accountSum = account.sum
                            )
                        )
                    }
                }

                accountInteractor
                    .getAllAccounts()
                    .map { accounts ->
                        accounts.filter { it.id != accountId }.reversed()
                    }
                    .first()
                    .also { accounts -> _uiState.update { it.copy(accounts = accounts) } }
            } else {
                _uiState.value = _uiState.value.copy(details = AccountDetails.CreateAccount)

                accountInteractor
                    .getAllAccounts()
                    .first()
                    .also { accounts -> _uiState.update { it.copy(accounts = accounts) } }
            }
        }
    }

    fun onUiEvent(uiEvent: AccountUiEvent) {
        when (uiEvent) {
            is AccountUiEvent.OnTransferAccountSelected -> {
                transferMoneyToAccountId(uiEvent.accountId)
                deleteAccount()
            }

            AccountUiEvent.OnConfirmAccountNameCollisionDialog -> {
                _uiState.update {
                    it.copy(
                        showAccountNameCollisionDialog = false,
                        accountNameError = true,
                        requestAccountNameFocus = true
                    )
                }
            }

            AccountUiEvent.OnConfirmDeleteAccountDialog -> {
                viewModelScope.launch {
                    if (checkAccountBalanceIsNotEmpty() && _uiState.value.accounts.isNotEmpty()) {
                        _uiState.update {
                            it.copy(
                                details = (it.details as AccountDetails.EditAccount).copy(
                                    showTransferAccountBalanceDialog = true,
                                    showDeleteAccountDialog = false
                                )
                            )
                        }
                    } else {
                        deleteAccount()
                    }
                }
            }

            AccountUiEvent.OnConfirmTransferAccountBalanceDialog -> {
                _uiState.update {
                    it.copy(
                        details = (it.details as AccountDetails.EditAccount).copy(
                            showTransferAccountBalanceDialog = false,
                            showSelectAccountDialog = true
                        )
                    )
                }
            }

            AccountUiEvent.OnDeleteIconClick -> {
                _uiState.update {
                    it.copy(
                        details = (it.details as AccountDetails.EditAccount).copy(
                            showDeleteAccountDialog = true
                        )
                    )
                }
            }

            AccountUiEvent.OnDismissDialog -> {
                _uiState.update {
                    it.copy(
                        details = (it.details as AccountDetails.EditAccount).copy(
                            showDeleteAccountDialog = false,
                            showTransferAccountBalanceDialog = false,
                            showSelectAccountDialog = false
                        )
                    )
                }
            }

            AccountUiEvent.OnDismissTransferAccountBalanceDialog -> deleteAccount()

            AccountUiEvent.OnSaveButtonClick -> saveAccount()

            AccountUiEvent.OnFocusRequested -> {
                _uiState.update {
                    it.copy(
                        requestAccountSumFocus = false,
                        requestAccountNameFocus = false
                    )
                }
            }
        }
    }

    private fun saveAccount() {
        if (!validateAccount()) return

        val accountSum = _uiState.value.accountSum
        val accountName = _uiState.value.accountName.trim()

        viewModelScope.launch {
            when (_uiState.value.details) {
                AccountDetails.CreateAccount -> {
                    accountInteractor.addAccount(
                        Account(
                            id = 0,
                            name = accountName,
                            sum = accountSum.toLongOrNull() ?: 0
                        )
                    )
                }

                is AccountDetails.EditAccount -> {
                    accountId?.let {
                        accountInteractor.getAccountById(accountId).also { account ->
                            accountInteractor.updateAccount(
                                account.copy(
                                    name = accountName,
                                    sum = accountSum.toLongOrNull() ?: 0
                                )
                            )
                        }
                    }
                }

                AccountDetails.Initial -> {
                    throw RuntimeException("AccountDetails must be CreateAccount or EditAccount")
                }
            }

            _uiState.update { it.copy(closeScreen = true) }
        }
    }

    private fun validateAccount(): Boolean {
        val accountSum = _uiState.value.accountSum
        val accountName = _uiState.value.accountName.trim()

        when {
            accountSum.isNotEmpty() && accountSum.toLongOrNull() == null -> {
                _uiState.update {
                    it.copy(
                        accountSumError = true,
                        requestAccountSumFocus = true
                    )
                }
            }

            accountName.isBlank() || accountName.isEmpty() -> {
                _uiState.update {
                    it.copy(
                        accountNameError = true,
                        requestAccountNameFocus = true
                    )
                }
            }

            checkAccountNameCollision(accountName) -> {
                _uiState.update { it.copy(showAccountNameCollisionDialog = true) }
            }

            else -> return true
        }

        return false
    }

    private fun checkAccountNameCollision(accountName: String): Boolean {
        return _uiState.value.accounts.find { it.name == accountName } != null
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            accountId?.let {
                accountInteractor
                    .getAccountById(accountId)
                    .also { account ->
                        accountInteractor.deleteAccount(account)
                    }
            }

            _uiState.update {
                it.copy(
                    closeScreen = true,
                    details = (it.details as AccountDetails.EditAccount).copy(
                        showTransferAccountBalanceDialog = false,
                        showDeleteAccountDialog = false,
                        showSelectAccountDialog = false
                    )
                )
            }
        }
    }

    private fun transferMoneyToAccountId(transferToAccountId: Int) {
        viewModelScope.launch {
            accountId?.let {
                val account = accountInteractor.getAccountById(accountId)

                accountInteractor
                    .getAccountById(transferToAccountId)
                    .also { accountInteractor.updateAccount(it.copy(sum = it.sum + account.sum)) }
            }
        }
    }

    fun updateAccountSum(accountSum: String) {
        if (accountSum.isDigitsOnly() && accountSum.length <= 12) {
            _uiState.update {
                it.copy(
                    accountSumError = false,
                    requestAccountSumFocus = false,
                    accountSum = accountSum
                )
            }
        }
    }

    fun updateAccountName(accountName: String) {
        if (accountName.length <= 50) {
            _uiState.update {
                it.copy(
                    accountNameError = false,
                    requestAccountNameFocus = false,
                    accountName = accountName
                )
            }
        }
    }

    private suspend fun checkAccountBalanceIsNotEmpty(): Boolean {
        return viewModelScope.async {
            accountId?.let {
                return@async accountInteractor.getAccountById(accountId).sum > 0
            }

            return@async false
        }.await()
    }
}