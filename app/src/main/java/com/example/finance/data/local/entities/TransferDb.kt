package com.example.finance.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = AccountDb::class,
            parentColumns = ["id"],
            childColumns = ["fromAccountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountDb::class,
            parentColumns = ["id"],
            childColumns = ["toAccountId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransferDb(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromAccountId: Int,
    val toAccountId: Int,
    val sum: Long,
    val date: LocalDate,
    val comment: String
)
