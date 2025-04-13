package com.example.finance.data.local.entities.mappers

import com.example.finance.data.local.entities.TransferDb
import com.example.finance.domain.entities.Transfer

fun Transfer.toDb(): TransferDb = TransferDb(
    id = this.id,
    fromAccountId = this.fromAccount.id,
    toAccountId = this.toAccount.id,
    sum = this.sum,
    date = this.date,
    comment = this.comment
)

fun List<Transfer>.toDb(): List<TransferDb> = this.map { it.toDb() }