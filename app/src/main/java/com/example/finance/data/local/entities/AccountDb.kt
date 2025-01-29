package com.example.finance.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountDb(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val sum: Long
)
