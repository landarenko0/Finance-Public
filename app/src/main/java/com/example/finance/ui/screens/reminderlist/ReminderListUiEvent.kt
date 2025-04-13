package com.example.finance.ui.screens.reminderlist

sealed interface ReminderListUiEvent {

    data class OnReminderSwitchClick(
        val permissionGranted: Boolean,
        val reminderId: Int
    ) : ReminderListUiEvent

    data object OnDismissDialog : ReminderListUiEvent

    data object OnPermissionGranted : ReminderListUiEvent

    data object OnPermissionNotGranted : ReminderListUiEvent
}