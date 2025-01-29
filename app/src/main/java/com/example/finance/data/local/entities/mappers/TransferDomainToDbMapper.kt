package com.example.finance.data.local.entities.mappers

import com.example.finance.data.local.entities.TransferDb
import com.example.finance.domain.entities.Transfer

class TransferDomainToDbMapper : (Transfer) -> TransferDb {

    override fun invoke(transfer: Transfer): TransferDb = TransferDb(
        id = transfer.id,
        fromAccountId = transfer.fromAccount.id,
        toAccountId = transfer.toAccount.id,
        sum = transfer.sum,
        date = transfer.date,
        comment = transfer.comment
    )
}