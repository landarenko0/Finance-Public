package com.example.finance.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.finance.domain.entities.OperationType

@Entity(
    indices = [
        Index("name", "type", unique = true)
    ]
)
data class CategoryDb(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: OperationType
)
