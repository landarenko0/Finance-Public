package com.example.finance.domain.usecases

import com.example.finance.domain.datastore.AccountIdRepository
import com.example.finance.domain.entities.Account
import com.example.finance.domain.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AccountInteractor(
    val getAllAccounts: GetAllUseCase<Account>,
    val getAccountById: GetObjectByIdUseCase<Account>,
    val addAccount: InsertUseCase<Account>,
    val updateAccount: UpdateUseCase<Account>,
    val deleteAccount: DeleteUseCase<Account>,
    val getAccountsTotalSum: GetAccountsTotalSumUseCase,
    val transferMoneyFromOneAccountToAnother: TransferMoneyFromOneAccountToAnotherUseCase,
    val checkAccountNameCollision: CheckAccountNameCollisionUseCase,
    val checkAccountNameCollisionExcept: CheckAccountNameCollisionExceptUseCase,
    val addMoneyToAccount: AddMoneyToAccountUseCase,
    val getSavedAccountId: GetSavedAccountIdUseCase,
    val updateSavedAccountId: UpdateSavedAccountIdUseCase,
    val flowAccountById: FlowAccountByIdUseCase,
    val flowTotalAccount: FlowTotalAccountUseCase
)

class GetAccountsTotalSumUseCase(private val repository: AccountRepository) {

    suspend operator fun invoke(): Long? = repository.getAccountsTotalSum()
}

class TransferMoneyFromOneAccountToAnotherUseCase(private val repository: AccountRepository) {

    suspend operator fun invoke(
        fromAccountId: Int,
        toAccountId: Int,
        sum: Long
    ) {
        val fromAccount = repository.getObjectById(fromAccountId)
        val toAccount = repository.getObjectById(toAccountId)

        repository.update(fromAccount.copy(balance = fromAccount.balance - sum))
        repository.update(toAccount.copy(balance = toAccount.balance + sum))
    }
}

class CheckAccountNameCollisionUseCase(private val repository: AccountRepository) {

    suspend operator fun invoke(accountName: String): Boolean {
        val accounts = repository.getAll().first()

        return withContext(Dispatchers.Default) { accounts.find { it.name == accountName } != null }
    }
}

class CheckAccountNameCollisionExceptUseCase(private val repository: AccountRepository) {

    suspend operator fun invoke(accountName: String, exceptAccountId: Int): Boolean {
        val accounts = repository.getAll().first()

        return withContext(Dispatchers.Default) {
            accounts.find { it.name == accountName && it.id != exceptAccountId } != null
        }
    }
}

class AddMoneyToAccountUseCase(private val repository: AccountRepository) {

    suspend operator fun invoke(accountId: Int, sum: Long) {
        repository.getObjectById(accountId).also { account ->
            repository.update(account.copy(balance = account.balance + sum))
        }
    }
}

class GetSavedAccountIdUseCase(private val repository: AccountIdRepository) {

    operator fun invoke(): Flow<Int?> = repository.data
}

class UpdateSavedAccountIdUseCase(private val repository: AccountIdRepository) {

    suspend operator fun invoke(accountId: Int) = repository.updateAccountId(accountId)
}

class FlowAccountByIdUseCase(private val repository: AccountRepository) {

    operator fun invoke(accountId: Int): Flow<Account?> = repository.flowAccountById(accountId)
}

class FlowTotalAccountUseCase(private val repository: AccountRepository) {

    operator fun invoke(): Flow<Account> = repository.flowTotalAccount()
}