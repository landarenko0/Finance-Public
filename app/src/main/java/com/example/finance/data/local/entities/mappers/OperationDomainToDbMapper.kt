package com.example.finance.data.local.entities.mappers

import com.example.finance.data.local.entities.OperationDb
import com.example.finance.domain.entities.Operation

class OperationDomainToDbMapper : (Operation) -> OperationDb {

    override fun invoke(operation: Operation): OperationDb = OperationDb(
        id = operation.id,
        categoryId = operation.category.id,
        subcategoryId = operation.subcategory?.id,
        accountId = operation.account.id,
        sum = operation.sum,
        date = operation.date,
        comment = operation.comment
    )
}