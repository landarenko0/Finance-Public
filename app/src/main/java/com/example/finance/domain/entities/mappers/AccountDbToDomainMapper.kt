package com.example.finance.domain.entities.mappers

import com.example.finance.data.local.entities.AccountDb
import com.example.finance.domain.entities.Account

class AccountDbToDomainMapper : (AccountDb) -> Account {

    override fun invoke(accountDb: AccountDb): Account = Account(
        id = accountDb.id,
        name = accountDb.name,
        sum = accountDb.sum
    )
}