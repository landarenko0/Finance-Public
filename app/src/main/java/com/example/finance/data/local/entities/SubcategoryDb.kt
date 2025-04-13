package com.example.finance.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = CategoryDb::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("name", "categoryId", unique = true),
        Index("categoryId")
    ]
)
data class SubcategoryDb(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val categoryId: Int
)
