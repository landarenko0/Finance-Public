package com.example.finance.domain.entities.mappers

import com.example.finance.data.local.entities.TransferDbExtended
import com.example.finance.domain.entities.Transfer

class TransferDbExtendedToDomainMapper(
    private val accountDbToDomainMapper: AccountDbToDomainMapper
) : (TransferDbExtended) -> Transfer {

    override fun invoke(transferDb: TransferDbExtended): Transfer = Transfer(
        id = transferDb.transfer.id,
        fromAccount = accountDbToDomainMapper(transferDb.fromAccount),
        toAccount = accountDbToDomainMapper(transferDb.toAccount),
        sum = transferDb.transfer.sum,
        date = transferDb.transfer.date,
        comment = transferDb.transfer.comment
    )
}