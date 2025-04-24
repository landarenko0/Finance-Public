package com.example.finance.data.local.entities.mappers.domainToDb

import com.example.finance.data.local.entities.AccountDb
import com.example.finance.domain.entities.Account

fun Account.toDb(): AccountDb = AccountDb(
    id = this.id,
    name = this.name,
    sum = this.balance
)

fun List<Account>.toDb(): List<AccountDb> = this.map { it.toDb() }