package com.example.finance.ui.screens.account

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.finance.domain.entities.Account
import com.example.finance.domain.usecases.AccountInteractor
import com.example.finance.ui.navigation.AccountScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _event = Channel<AccountEvent>()
    val event = _event.receiveAsFlow()

    val accountId = savedStateHandle.toRoute<AccountScreen>().accountId

    init {
        viewModelScope.launch {
            if (accountId != null) {
                accountInteractor.getAccountById(accountId).also { account ->
                    _uiState.update { state ->
                        state.copy(
                            accountBalance = account.balance.toString(),
                            accountName = account.name,
                            details = AccountDetails.EditAccount(
                                otherAccounts = accountInteractor.getAllAccounts()
                                    .map { accounts -> accounts.filter { it.id != accountId } }
                                    .flowOn(Dispatchers.Default)
                                    .first(),
                                accountBalance = account.balance
                            )
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(details = AccountDetails.CreateAccount) }
            }
        }
    }

    fun onUiEvent(uiEvent: AccountUiEvent) {
        when (uiEvent) {
            is AccountUiEvent.OnTransferAccountSelected -> {
                viewModelScope.launch {
                    transferMoneyToAccountId(uiEvent.accountId)
                    deleteAccount()
                }
            }

            AccountUiEvent.OnConfirmAccountNameCollisionDialog -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            showAccountNameCollisionDialog = false,
                            accountNameError = true
                        )
                    }

                    _event.send(AccountEvent.RequestAccountNameFocus)
                }
            }

            AccountUiEvent.OnConfirmDeleteAccountDialog -> {
                val details = _uiState.value.details as AccountDetails.EditAccount

                if (details.accountBalance > 0 && details.otherAccounts.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            details = details.copy(
                                showTransferAccountBalanceDialog = true,
                                showDeleteAccountDialog = false
                            )
                        )
                    }
                } else {
                    deleteAccount()
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

            AccountUiEvent.OnBackIconClick -> {
                viewModelScope.launch { _event.send(AccountEvent.CloseScreen) }
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

            is AccountUiEvent.OnAccountBalanceChanged -> {
                if (uiEvent.accountBalance.isDigitsOnly() && uiEvent.accountBalance.length <= 12) {
                    _uiState.update {
                        it.copy(
                            accountBalanceError = false,
                            accountBalance = uiEvent.accountBalance
                        )
                    }
                }
            }

            is AccountUiEvent.OnAccountNameChanged -> {
                if (uiEvent.accountName.length <= 50) {
                    _uiState.update {
                        it.copy(
                            accountNameError = false,
                            accountName = uiEvent.accountName
                        )
                    }
                }
            }
        }
    }

    private fun saveAccount() {
        viewModelScope.launch {
            val accountName = _uiState.value.accountName.trim()
            val accountSum = _uiState.value.accountBalance

            if (!validateInput(accountName, accountSum)) return@launch

            when (_uiState.value.details) {
                AccountDetails.CreateAccount -> {
                    accountInteractor.addAccount(
                        Account(
                            id = 0,
                            name = accountName,
                            balance = accountSum.toLongOrNull() ?: 0
                        )
                    )
                }

                is AccountDetails.EditAccount -> {
                    accountId?.let {
                        accountInteractor.getAccountById(accountId).also { account ->
                            accountInteractor.updateAccount(
                                account.copy(
                                    name = accountName,
                                    balance = accountSum.toLongOrNull() ?: 0
                                )
                            )
                        }
                    }
                }

                AccountDetails.Initial -> {
                    throw RuntimeException("AccountDetails must be CreateAccount or EditAccount")
                }
            }

            _event.send(AccountEvent.CloseScreen)
        }
    }

    private suspend fun validateInput(
        accountName: String,
        accountSum: String
    ): Boolean = viewModelScope.async {
        when {
            accountSum.isNotEmpty() && accountSum.toLongOrNull() == null -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(accountBalanceError = true) }
                    _event.send(AccountEvent.RequestAccountBalanceFocus)
                }
            }

            accountName.isBlank() || accountName.isEmpty() -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(accountNameError = true) }
                    _event.send(AccountEvent.RequestAccountNameFocus)
                }
            }

            _uiState.value.details is AccountDetails.CreateAccount && accountInteractor.checkAccountNameCollision(accountName) -> {
                _uiState.update { it.copy(showAccountNameCollisionDialog = true) }
            }

            _uiState.value.details is AccountDetails.EditAccount && accountInteractor.checkAccountNameCollisionExcept(accountName, accountId!!) -> {
                _uiState.update { it.copy(showAccountNameCollisionDialog = true) }
            }

            else -> return@async true
        }

        return@async false
    }.await()

    private fun deleteAccount() {
        viewModelScope.launch {
            accountId?.let {
                accountInteractor
                    .getAccountById(accountId)
                    .also { account -> accountInteractor.deleteAccount(account) }
            }

            _uiState.update {
                it.copy(
                    details = (it.details as AccountDetails.EditAccount).copy(
                        showTransferAccountBalanceDialog = false,
                        showDeleteAccountDialog = false,
                        showSelectAccountDialog = false
                    )
                )
            }

            _event.send(AccountEvent.CloseScreen)
        }
    }

    private suspend fun transferMoneyToAccountId(transferToAccountId: Int) {
        accountId?.let {
            val fromAccount = accountInteractor.getAccountById(accountId)

            accountInteractor.transferMoneyFromOneAccountToAnother(
                fromAccountId = accountId,
                toAccountId = transferToAccountId,
                sum = fromAccount.balance
            )
        }
    }
}