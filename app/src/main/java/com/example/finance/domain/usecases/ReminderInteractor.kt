package com.example.finance.domain.usecases

import com.example.finance.domain.entities.Reminder
import com.example.finance.domain.repository.ReminderRepository
import com.example.finance.domain.scheduler.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ReminderInteractor(
    val getAllReminders: GetAllUseCase<Reminder>,
    val getReminderById: GetObjectByIdUseCase<Reminder>,
    val addReminder: InsertUseCase<Reminder>,
    val updateReminder: UpdateUseCase<Reminder>,
    val deleteReminder: DeleteUseCase<Reminder>,
    val checkReminderNameCollision: CheckReminderNameCollisionUseCase,
    val checkReminderNameCollisionExcept: CheckReminderNameCollisionExceptUseCase,
    val scheduleReminder: ScheduleReminderUseCase,
    val cancelReminder: CancelReminderUseCase
)

class CheckReminderNameCollisionUseCase(private val repository: ReminderRepository) {

    suspend operator fun invoke(reminderName: String): Boolean {
        val reminders = repository.getAll().first()

        return withContext(Dispatchers.Default) {
            reminders.find { it.name == reminderName } != null
        }
    }
}

class CheckReminderNameCollisionExceptUseCase(private val repository: ReminderRepository) {

    suspend operator fun invoke(reminderName: String, exceptReminderId: Int): Boolean {
        val reminders = repository.getAll().first()

        return withContext(Dispatchers.Default) {
            reminders.find { it.name == reminderName && it.id != exceptReminderId } != null
        }
    }
}

class ScheduleReminderUseCase(
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler
) {

    suspend operator fun invoke(reminder: Reminder) {
        repository.update(
            reminder.copy(
                isActive = true,
                workId = scheduler.scheduleReminder(reminder)
            )
        )
    }
}

class CancelReminderUseCase(
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler
) {

    suspend operator fun invoke(reminder: Reminder) {
        scheduler.cancelReminder(reminder)

        repository.update(
            reminder.copy(
                isActive = false,
                workId = null
            )
        )
    }
}