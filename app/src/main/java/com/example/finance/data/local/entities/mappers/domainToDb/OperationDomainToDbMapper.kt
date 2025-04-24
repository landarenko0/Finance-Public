package com.example.finance.data.local.entities.mappers.domainToDb

import com.example.finance.data.local.entities.OperationDb
import com.example.finance.domain.entities.Operation

fun Operation.toDb(): OperationDb = OperationDb(
    id = this.id,
    categoryId = this.category.id,
    subcategoryId = this.subcategory?.id,
    accountId = this.account.id,
    sum = this.sum,
    date = this.date,
    comment = this.comment
)

fun List<Operation>.toDb(): List<OperationDb> = this.map { it.toDb() }