package com.example.finance.ui.screens.reminderlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.domain.usecases.ReminderInteractor
import com.example.finance.utils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    private val reminderInteractor: ReminderInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderListUiState())
    val uiState: StateFlow<ReminderListUiState> = _uiState.asStateFlow()

    private val _event = Channel<ReminderListEvent>()
    val event = _event.receiveAsFlow()

    private var selectedReminderId: Int? = null

    init {
        reminderInteractor
            .getAllReminders()
            .onEach { reminders -> _uiState.update { it.copy(reminders = reminders) } }
            .launchIn(viewModelScope)
    }

    fun onUiEvent(uiEvent: ReminderListUiEvent) {
        when (uiEvent) {
            ReminderListUiEvent.OnDismissDialog -> {
                _uiState.update {
                    it.copy(
                        showPermissionNotGrantedDialog = false,
                        showInvalidReminderDateDialog = false
                    )
                }
            }

            ReminderListUiEvent.OnPermissionGranted -> {
                selectedReminderId?.let { updateReminder(true, it) }
                selectedReminderId = null
            }

            ReminderListUiEvent.OnPermissionNotGranted -> {
                _uiState.update { it.copy(showPermissionNotGrantedDialog = true) }
            }

            is ReminderListUiEvent.OnReminderSwitchClick -> {
                updateReminder(uiEvent.permissionGranted, uiEvent.reminderId)
            }
        }
    }

    private fun updateReminder(permissionGranted: Boolean, reminderId: Int) {
        viewModelScope.launch {
            reminderInteractor.getReminderById(reminderId).also { reminder ->
                if (reminder.isActive) {
                    reminderInteractor.cancelReminder(reminder)
                } else {
                    val initialDelay = reminder.date.toMillis() - System.currentTimeMillis()

                    when {
                        !permissionGranted -> {
                            selectedReminderId = reminderId
                            _event.send(ReminderListEvent.RequestPermission)
                        }

                        initialDelay < 0 -> {
                            _uiState.update { it.copy(showInvalidReminderDateDialog = true) }
                        }

                        else -> reminderInteractor.scheduleReminder(reminder)
                    }
                }
            }
        }
    }
}