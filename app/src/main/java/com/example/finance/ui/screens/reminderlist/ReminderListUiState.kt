package com.example.finance.ui.screens.reminderlist

import com.example.finance.domain.entities.Reminder

data class ReminderListUiState(
    val reminders: List<Reminder> = emptyList(),
    val showPermissionNotGrantedDialog: Boolean = false,
    val showInvalidReminderDateDialog: Boolean = false
)