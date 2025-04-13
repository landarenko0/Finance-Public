package com.example.finance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.finance.data.local.entities.ReminderDb
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao : BaseDao<ReminderDb> {

    @Query("SELECT * FROM reminderdb")
    fun getAll(): Flow<List<ReminderDb>>

    @Query("SELECT * FROM reminderdb WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: Int): ReminderDb
}