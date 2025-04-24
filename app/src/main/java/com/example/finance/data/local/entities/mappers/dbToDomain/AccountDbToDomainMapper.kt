package com.example.finance.data.local.entities.mappers.dbToDomain

import com.example.finance.data.local.entities.AccountDb
import com.example.finance.domain.entities.Account

fun AccountDb.toDomain(): Account = Account(
    id = this.id,
    name = this.name,
    balance = this.sum
)

fun List<AccountDb>.toDomain(): List<Account> = this.map { it.toDomain() }