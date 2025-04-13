package com.example.finance.ui.screens.reminderlist

sealed interface ReminderListEvent {

    data object RequestPermission : ReminderListEvent
}