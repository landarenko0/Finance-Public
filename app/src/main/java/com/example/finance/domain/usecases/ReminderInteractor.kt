package com.example.finance.domain.usecases

import com.example.finance.domain.entities.Reminder

class ReminderInteractor(
    val getAllReminders: GetAllUseCase<Reminder>,
    val getReminderById: GetObjectByIdUseCase<Reminder>,
    val addReminder: InsertUseCase<Reminder>,
    val addAllReminders: InsertAllUseCase<Reminder>,
    val updateReminder: UpdateUseCase<Reminder>,
    val deleteReminder: DeleteUseCase<Reminder>,
    val deleteReminderById: DeleteObjectByIdUseCase<Reminder>
)