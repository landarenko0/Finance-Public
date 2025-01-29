package com.example.finance.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class OperationDbExtended(
    @Embedded val operation: OperationDb,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryDb,
    @Relation(
        parentColumn = "subcategoryId",
        entityColumn = "id"
    )
    val subcategory: SubcategoryDb?,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "id"
    )
    val account: AccountDb
)