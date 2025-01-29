package com.example.finance.domain.repository

import kotlinx.coroutines.flow.Flow

interface BaseRepository<T> {

    fun getAll(): Flow<List<T>>

    suspend fun getObjectById(objectId: Int): T

    suspend fun insert(obj: T)

    suspend fun update(obj: T)

    suspend fun insertAll(objects: List<T>)

    suspend fun delete(obj: T)

    suspend fun deleteObjectById(objectId: Int)
}