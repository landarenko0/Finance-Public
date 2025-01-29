package com.example.finance.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class TransferDbExtended(
    @Embedded val transfer: TransferDb,
    @Relation(
        parentColumn = "fromAccountId",
        entityColumn = "id"
    )
    val fromAccount: AccountDb,
    @Relation(
        parentColumn = "toAccountId",
        entityColumn = "id"
    )
    val toAccount: AccountDb
)
