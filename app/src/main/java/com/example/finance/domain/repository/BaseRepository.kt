package com.example.finance.domain.repository

import kotlinx.coroutines.flow.Flow

interface BaseRepository<T> {

    fun getAll(): Flow<List<T>>

    suspend fun getObjectById(objectId: Int): T

    suspend fun insert(obj: T): Long

    suspend fun update(obj: T)

    suspend fun delete(obj: T)
}