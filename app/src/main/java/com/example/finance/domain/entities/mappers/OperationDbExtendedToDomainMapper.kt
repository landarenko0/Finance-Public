package com.example.finance.domain.entities.mappers

import com.example.finance.data.local.entities.OperationDbExtended
import com.example.finance.domain.entities.Operation

class OperationDbExtendedToDomainMapper(
    private val categoryDbToDomainMapper: CategoryDbToDomainMapper,
    private val subcategoryDbToDomainMapper: SubcategoryDbToDomainMapper,
    private val accountDbToDomainMapper: AccountDbToDomainMapper
) : (OperationDbExtended) -> Operation {

    override fun invoke(operationDb: OperationDbExtended): Operation = Operation(
        id = operationDb.operation.id,
        category = categoryDbToDomainMapper(operationDb.category),
        subcategory = operationDb.subcategory?.let(subcategoryDbToDomainMapper),
        account = accountDbToDomainMapper(operationDb.account),
        sum = operationDb.operation.sum,
        date = operationDb.operation.date,
        comment = operationDb.operation.comment
    )
}