package com.example.finance.ui.screens.accountlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.domain.usecases.AccountInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AccountListViewModel @Inject constructor(
    accountInteractor: AccountInteractor
) : ViewModel() {

    val uiState: StateFlow<AccountListUiState> = accountInteractor
        .getAllAccounts()
        .map { accounts ->
            AccountListUiState(
                accounts = accounts,
                totalBalance = accounts.sumOf { it.balance }
            )
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AccountListUiState()
        )
}