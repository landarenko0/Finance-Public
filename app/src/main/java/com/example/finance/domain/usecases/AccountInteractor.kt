package com.example.finance.domain.usecases

import com.example.finance.domain.entities.Account
import com.example.finance.domain.repository.AccountRepository

class AccountInteractor(
    val getAllAccounts: GetAllUseCase<Account>,
    val getAccountById: GetObjectByIdUseCase<Account>,
    val addAccount: InsertUseCase<Account>,
    val addAllAccounts: InsertAllUseCase<Account>,
    val updateAccount: UpdateUseCase<Account>,
    val deleteAccount: DeleteUseCase<Account>,
    val deleteAccountById: DeleteObjectByIdUseCase<Account>,
    val getAccountsTotalSum: GetAccountsTotalSumUseCase
)

class GetAccountsTotalSumUseCase(private val repository: AccountRepository) {

    suspend operator fun invoke(): Long? = repository.getAccountsTotalSum()
}