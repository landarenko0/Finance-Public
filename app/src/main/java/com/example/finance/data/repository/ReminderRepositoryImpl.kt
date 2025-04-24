package com.example.finance.data.repository

import com.example.finance.data.local.dao.ReminderDao
import com.example.finance.data.local.entities.mappers.domainToDb.toDb
import com.example.finance.domain.entities.Reminder
import com.example.finance.data.local.entities.mappers.dbToDomain.toDomain
import com.example.finance.domain.repository.ReminderRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
    private val dispatcher: CoroutineDispatcher
) : ReminderRepository {

    override fun getAll(): Flow<List<Reminder>> = reminderDao.getAll()
        .map { it.toDomain() }
        .flowOn(dispatcher)

    override suspend fun getObjectById(objectId: Int): Reminder =
        withContext(dispatcher) { reminderDao.getReminderById(objectId).toDomain() }

    override suspend fun insert(obj: Reminder) =
        withContext(dispatcher) { reminderDao.insert(obj.toDb()) }

    override suspend fun update(obj: Reminder) =
        withContext(dispatcher) { reminderDao.update(obj.toDb()) }

    override suspend fun delete(obj: Reminder) =
        withContext(dispatcher) { reminderDao.delete(obj.toDb()) }
}