package com.example.finance.domain.entities

import java.time.LocalDate

data class Operation(
    val id: Int,
    val category: Category,
    val subcategory: Subcategory?,
    val account: Account,
    val sum: Long,
    val date: LocalDate,
    val comment: String
)