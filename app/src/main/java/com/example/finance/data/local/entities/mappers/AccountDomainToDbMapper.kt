package com.example.finance.data.local.entities.mappers

import com.example.finance.data.local.entities.AccountDb
import com.example.finance.domain.entities.Account

class AccountDomainToDbMapper : (Account) -> AccountDb {

    override fun invoke(account: Account): AccountDb = AccountDb(
        id = account.id,
        name = account.name,
        sum = account.sum
    )
}