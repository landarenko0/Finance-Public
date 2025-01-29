package com.example.finance.domain.entities

data class GroupedCategories(
    val operationType: OperationType,
    val categoryName: String,
    val totalSum: Long,
    val categoryId: Int?
)
