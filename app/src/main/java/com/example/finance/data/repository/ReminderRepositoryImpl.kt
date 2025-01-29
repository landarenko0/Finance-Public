package com.example.finance.data.repository

import com.example.finance.data.local.dao.ReminderDao
import com.example.finance.data.local.entities.mappers.ReminderDomainToDbMapper
import com.example.finance.domain.entities.Reminder
import com.example.finance.domain.entities.mappers.ReminderDbToDomainMapper
import com.example.finance.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
    private val reminderDbToDomainMapper: ReminderDbToDomainMapper,
    private val reminderDomainToDbMapper: ReminderDomainToDbMapper
) : ReminderRepository {

    override fun getAll(): Flow<List<Reminder>> = reminderDao.getAll().map { reminders ->
        reminders.map(reminderDbToDomainMapper)
    }

    override suspend fun getObjectById(objectId: Int): Reminder {
        val reminderDb = reminderDao.getReminderById(objectId)
        return reminderDbToDomainMapper(reminderDb)
    }

    override suspend fun insert(obj: Reminder) = reminderDao.insert(reminderDomainToDbMapper(obj))

    override suspend fun insertAll(objects: List<Reminder>) =
        reminderDao.insertAll(objects.map(reminderDomainToDbMapper))

    override suspend fun update(obj: Reminder) = reminderDao.update(reminderDomainToDbMapper(obj))

    override suspend fun delete(obj: Reminder) = reminderDao.delete(reminderDomainToDbMapper(obj))

    override suspend fun deleteObjectById(objectId: Int) = reminderDao.deleteReminderById(objectId)
}