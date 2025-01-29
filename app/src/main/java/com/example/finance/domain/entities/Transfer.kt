package com.example.finance.domain.entities

import java.time.LocalDate

data class Transfer(
    val id: Int,
    val fromAccount: Account,
    val toAccount: Account,
    val sum: Long,
    val date: LocalDate,
    val comment: String
)