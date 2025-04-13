package com.example.finance.domain.entities.mappers

import com.example.finance.data.local.entities.TransferDbExtended
import com.example.finance.domain.entities.Transfer

fun TransferDbExtended.toDomain(): Transfer = Transfer(
    id = this.transfer.id,
    fromAccount = this.fromAccount.toDomain(),
    toAccount = this.toAccount.toDomain(),
    sum = this.transfer.sum,
    date = this.transfer.date,
    comment = this.transfer.comment
)

fun List<TransferDbExtended>.toDomain(): List<Transfer> = this.map { it.toDomain() }