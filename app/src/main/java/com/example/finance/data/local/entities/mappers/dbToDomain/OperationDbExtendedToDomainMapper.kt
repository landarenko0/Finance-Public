package com.example.finance.data.local.entities.mappers.dbToDomain

import com.example.finance.data.local.entities.OperationDbExtended
import com.example.finance.domain.entities.Operation

fun OperationDbExtended.toDomain(): Operation = Operation(
    id = this.operation.id,
    category = this.category.toDomain(),
    subcategory = this.subcategory?.toDomain(),
    account = this.account.toDomain(),
    sum = this.operation.sum,
    date = this.operation.date,
    comment = this.operation.comment
)

fun List<OperationDbExtended>.toDomain(): List<Operation> = this.map { it.toDomain() }