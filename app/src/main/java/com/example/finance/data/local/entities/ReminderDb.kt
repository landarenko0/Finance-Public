package com.example.finance.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.finance.domain.entities.Periodicity
import java.time.LocalDateTime

@Entity(
    indices = [
        Index("name", unique = true)
    ]
)
data class ReminderDb(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val periodicity: Periodicity,
    val date: LocalDateTime,
    val comment: String,
    val isActive: Boolean,
    val workId: String?
)
