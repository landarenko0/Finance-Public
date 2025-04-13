package com.example.finance.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
        ),
        ForeignKey(
            entity = SubcategoryDb::class,
            parentColumns = ["id"],
            childColumns = ["subcategoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("categoryId"),
        Index("subcategoryId"),
        Index("accountId")
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