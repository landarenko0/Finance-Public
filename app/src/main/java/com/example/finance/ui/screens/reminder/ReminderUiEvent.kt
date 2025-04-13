package com.example.finance.ui.screens.reminder

import com.example.finance.domain.entities.Periodicity

sealed interface ReminderUiEvent {

    data class OnPeriodicitySelected(val periodicity: Periodicity) : ReminderUiEvent

    data class OnDateSelected(val date: Long) : ReminderUiEvent

    data class OnTimeSelected(val time: Pair<Int, Int>) : ReminderUiEvent

    data class OnSaveButtonClick(val permissionGranted: Boolean) : ReminderUiEvent

    data object OnDeleteIconClick : ReminderUiEvent

    data object OnConfirmDeleteReminderDialog : ReminderUiEvent

    data object OnDismissDialog : ReminderUiEvent

    data class OnPermissionDialogDismiss(val permissionGranted: Boolean) : ReminderUiEvent

    data object OnInvalidReminderDateConfirm : ReminderUiEvent

    data object OnDismissDeleteReminderDialog : ReminderUiEvent

    data object OnPeriodicityPickerClick : ReminderUiEvent

    data object OnDatePickerClick : ReminderUiEvent

    data object OnTimePickerClick : ReminderUiEvent

    data class OnReminderNameChanged(val reminderName: String) : ReminderUiEvent

    data class OnCommentChanged(val comment: String) : ReminderUiEvent

    data object OnBackIconClick : ReminderUiEvent
}