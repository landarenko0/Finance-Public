package com.example.finance.ui.screens.accountlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.domain.usecases.AccountInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountListViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountListUiState())
    val uiState: StateFlow<AccountListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            accountInteractor
                .getAllAccounts()
                .map { it.reversed() }
                .collect { accounts ->
                    _uiState.update {
                        it.copy(
                            accounts = accounts,
                            totalSum = accounts.sumOf { account -> account.sum }
                        )
                    }
                }
        }
    }
}