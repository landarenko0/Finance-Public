package com.example.finance.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T> {

    @Insert
    suspend fun insert(obj: T): Long

    @Insert
    suspend fun insertAll(objects: List<T>)

    @Update
    suspend fun update(obj: T)

    @Delete
    suspend fun delete(obj: T)
}