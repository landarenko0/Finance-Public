package com.example.finance.ui.screens.reminder

import com.example.finance.domain.entities.Periodicity

data class ReminderUiState(
    val reminderName: String = "",
    val periodicity: Periodicity = Periodicity.ONCE,
    val selectedDate: Long = System.currentTimeMillis(),
    val selectedTime: Pair<Int, Int> = 12 to 0, // hours to minutes
    val comment: String = "",
    val showPeriodicityDialog: Boolean = false,
    val showDatePickerDialog: Boolean = false,
    val showTimePickerDialog: Boolean = false,
    val showReminderNameCollisionDialog: Boolean = false,
    val showInvalidReminderDateDialog: Boolean = false,
    val reminderNameError: Boolean = false,
    val details: ReminderDetails = ReminderDetails.Initial
)

sealed interface ReminderDetails {

    data object Initial : ReminderDetails

    data object CreateReminder : ReminderDetails

    data class EditReminder(val showDeleteReminderDialog: Boolean = false) : ReminderDetails
}