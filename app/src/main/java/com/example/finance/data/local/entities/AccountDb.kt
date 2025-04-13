package com.example.finance.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index("name", unique = true)
    ]
)
data class AccountDb(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val sum: Long
)
