package com.example.finance.ui.screens.reminder

sealed interface ReminderEvent {

    data object CloseScreen : ReminderEvent

    data object RequestPermission : ReminderEvent

    data object RequestReminderNameFocus : ReminderEvent
}