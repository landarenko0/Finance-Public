package com.example.finance.ui.screens.reminder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.finance.domain.entities.Reminder
import com.example.finance.domain.usecases.ReminderInteractor
import com.example.finance.ui.navigation.ReminderScreen
import com.example.finance.utils.toLocalDateTime
import com.example.finance.utils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reminderInteractor: ReminderInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    private val _event = Channel<ReminderEvent>()
    val event = _event.receiveAsFlow()

    private val reminderId = savedStateHandle.toRoute<ReminderScreen>().reminderId

    init {
        viewModelScope.launch {
            if (reminderId != null) {
                reminderInteractor.getReminderById(reminderId).also { reminder ->
                    _uiState.update {
                        it.copy(
                            reminderName = reminder.name,
                            periodicity = reminder.periodicity,
                            selectedDate = reminder.date.toMillis(),
                            selectedTime = reminder.date.hour to reminder.date.minute,
                            comment = reminder.comment,
                            details = ReminderDetails.EditReminder()
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(details = ReminderDetails.CreateReminder) }
            }
        }
    }

    fun onUiEvent(uiEvent: ReminderUiEvent) {
        when (uiEvent) {
            ReminderUiEvent.OnDatePickerClick -> {
                _uiState.update { it.copy(showDatePickerDialog = true) }
            }

            ReminderUiEvent.OnDeleteIconClick -> {
                _uiState.update {
                    it.copy(
                        details = (it.details as ReminderDetails.EditReminder).copy(
                            showDeleteReminderDialog = true
                        )
                    )
                }
            }

            ReminderUiEvent.OnConfirmDeleteReminderDialog -> deleteReminder()

            ReminderUiEvent.OnDismissDeleteReminderDialog -> {
                _uiState.update {
                    it.copy(
                        details = (it.details as ReminderDetails.EditReminder).copy(
                            showDeleteReminderDialog = false
                        )
                    )
                }
            }

            ReminderUiEvent.OnDismissDialog -> {
                _uiState.update {
                    it.copy(
                        showPeriodicityDialog = false,
                        showDatePickerDialog = false,
                        showTimePickerDialog = false,
                        showReminderNameCollisionDialog = false,
                        showInvalidReminderDateDialog = false
                    )
                }
            }

            is ReminderUiEvent.OnDateSelected -> {
                _uiState.update {
                    it.copy(
                        selectedDate = uiEvent.date,
                        showDatePickerDialog = false
                    )
                }
            }

            is ReminderUiEvent.OnPeriodicitySelected -> {
                _uiState.update {
                    it.copy(
                        periodicity = uiEvent.periodicity,
                        showPeriodicityDialog = false
                    )
                }
            }

            is ReminderUiEvent.OnTimeSelected -> {
                _uiState.update {
                    it.copy(
                        selectedTime = uiEvent.time,
                        showTimePickerDialog = false
                    )
                }
            }

            ReminderUiEvent.OnPeriodicityPickerClick -> {
                _uiState.update { it.copy(showPeriodicityDialog = true) }
            }

            is ReminderUiEvent.OnSaveButtonClick -> onSaveButtonClick(uiEvent.permissionGranted)

            ReminderUiEvent.OnTimePickerClick -> {
                _uiState.update { it.copy(showTimePickerDialog = true) }
            }

            is ReminderUiEvent.OnPermissionDialogDismiss -> {
                if (calculateInitialDelay() < 0) {
                    _uiState.update { it.copy(showInvalidReminderDateDialog = true) }
                } else {
                    saveReminder(uiEvent.permissionGranted)
                }
            }

            ReminderUiEvent.OnInvalidReminderDateConfirm -> saveReminder(false)

            is ReminderUiEvent.OnCommentChanged -> {
                if (uiEvent.comment.length <= 100) {
                    _uiState.update { it.copy(comment = uiEvent.comment) }
                }
            }

            is ReminderUiEvent.OnReminderNameChanged -> {
                if (uiEvent.reminderName.length <= 50) {
                    _uiState.update {
                        it.copy(
                            reminderName = uiEvent.reminderName,
                            reminderNameError = false
                        )
                    }
                }
            }

            ReminderUiEvent.OnBackIconClick -> {
                viewModelScope.launch { _event.send(ReminderEvent.CloseScreen) }
            }
        }
    }

    private fun onSaveButtonClick(permissionGranted: Boolean) {
        when {
            !permissionGranted -> {
                viewModelScope.launch { _event.send(ReminderEvent.RequestPermission) }
            }

            calculateInitialDelay() < 0 -> {
                _uiState.update { it.copy(showInvalidReminderDateDialog = true) }
            }

            else -> saveReminder(true)
        }
    }

    private fun saveReminder(enableReminder: Boolean) {
        viewModelScope.launch {
            val reminderName = _uiState.value.reminderName.trim()

            if (!validateInput(reminderName)) return@launch

            val periodicity = _uiState.value.periodicity

            val date = _uiState.value.selectedDate.toLocalDateTime()
            val hours = _uiState.value.selectedTime.first
            val minutes = _uiState.value.selectedTime.second
            val dateTime = LocalDateTime.of(date.year, date.month, date.dayOfMonth, hours, minutes)

            val comment = _uiState.value.comment.trim()

            when (_uiState.value.details) {
                ReminderDetails.CreateReminder -> {
                    val reminderId = reminderInteractor.addReminder(
                        Reminder(
                            id = 0,
                            name = reminderName,
                            periodicity = periodicity,
                            date = dateTime,
                            comment = comment,
                            isActive = enableReminder,
                            workId = null
                        )
                    )

                    if (enableReminder) {
                        reminderInteractor.getReminderById(reminderId.toInt()).also { reminder ->
                            reminderInteractor.scheduleReminder(reminder)
                        }
                    }
                }

                is ReminderDetails.EditReminder -> {
                    reminderId?.let {
                        val oldReminder = reminderInteractor.getReminderById(reminderId)

                        reminderInteractor.updateReminder(
                            oldReminder.copy(
                                name = reminderName,
                                periodicity = periodicity,
                                date = dateTime,
                                comment = comment
                            )
                        )

                        val newReminder = reminderInteractor.getReminderById(oldReminder.id)

                        if (oldReminder.isActive) {
                            reminderInteractor.cancelReminder(oldReminder)
                            reminderInteractor.scheduleReminder(newReminder)
                        }
                    }
                }

                ReminderDetails.Initial -> {
                    throw RuntimeException("ReminderDetails must be CreateReminder or EditReminder")
                }
            }

            _uiState.update { it.copy(showInvalidReminderDateDialog = false) }
            _event.send(ReminderEvent.CloseScreen)
        }
    }

    private suspend fun validateInput(reminderName: String): Boolean {
        when {
            reminderName.isEmpty() || reminderName.isBlank() -> {
                _uiState.update { it.copy(reminderNameError = true) }
                _event.send(ReminderEvent.RequestReminderNameFocus)
            }

            _uiState.value.details is ReminderDetails.CreateReminder && reminderInteractor.checkReminderNameCollision(reminderName) -> {
                _uiState.update {
                    it.copy(
                        showReminderNameCollisionDialog = true,
                        reminderNameError = true
                    )
                }

                _event.send(ReminderEvent.RequestReminderNameFocus)
            }

            _uiState.value.details is ReminderDetails.EditReminder && reminderInteractor.checkReminderNameCollisionExcept(reminderName, reminderId!!) -> {
                _uiState.update {
                    it.copy(
                        showReminderNameCollisionDialog = true,
                        reminderNameError = true
                    )
                }

                _event.send(ReminderEvent.RequestReminderNameFocus)
            }

            else -> return true
        }

        return false
    }

    private fun deleteReminder() {
        viewModelScope.launch {
            reminderId?.let {
                reminderInteractor.getReminderById(reminderId).also {
                    reminderInteractor.deleteReminder(it)
                    reminderInteractor.cancelReminder(it)
                }
            }

            _uiState.update {
                it.copy(
                    details = (it.details as ReminderDetails.EditReminder).copy(
                        showDeleteReminderDialog = false
                    )
                )
            }

            _event.send(ReminderEvent.CloseScreen)
        }
    }

    private fun calculateInitialDelay(): Long {
        val date = _uiState.value.selectedDate.toLocalDateTime()
        val hours = _uiState.value.selectedTime.first
        val minutes = _uiState.value.selectedTime.second
        val dateTime = LocalDateTime.of(date.year, date.month, date.dayOfMonth, hours, minutes)

        return dateTime.toMillis() - System.currentTimeMillis()
    }
}