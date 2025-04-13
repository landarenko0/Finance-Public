package com.example.finance.data.local.entities

import com.example.finance.domain.entities.OperationType

data class GroupedCategoriesDb(
    val operationType: OperationType,
    val categoryName: String,
    val totalSum: Long,
    val categoryId: Int?
)