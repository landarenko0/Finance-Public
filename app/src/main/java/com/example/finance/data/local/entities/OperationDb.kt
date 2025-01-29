package com.example.finance.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CategoryDb::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountDb::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OperationDb(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int,
    val subcategoryId: Int?,
    val accountId: Int,
    val sum: Long,
    val date: LocalDate,
    val comment: String
)