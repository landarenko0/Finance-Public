package com.example.finance.ui.screens.reminder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.domain.entities.Periodicity
import com.example.finance.ui.common.BackTopBar
import com.example.finance.ui.common.ConfirmationDialog
import com.example.finance.ui.common.DatePickerModalDialog
import com.example.finance.ui.common.FinanceTextField
import com.example.finance.ui.common.MessageToUserDialog
import com.example.finance.ui.common.PickerWithTitle
import com.example.finance.ui.common.PresentOrFutureSelectableDates
import com.example.finance.ui.common.SaveButton
import com.example.finance.ui.common.TimePickerModalDialog
import com.example.finance.ui.screens.reminder.components.PeriodicityDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReminderScreen(
    navigateBack: () -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current

    var notificationsPermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val requestNotificationsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        notificationsPermissionGranted = it
        viewModel.onUiEvent(ReminderUiEvent.OnPermissionDialogDismiss(it))
    }

    val reminderNameFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                ReminderEvent.CloseScreen -> navigateBack()

                ReminderEvent.RequestPermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestNotificationsPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                }

                ReminderEvent.RequestReminderNameFocus -> reminderNameFocusRequester.requestFocus()
            }
        }
    }

    val modifier = Modifier
        .fillMaxSize()
        .clickable(
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
            },
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        )

    when (uiState.details) {
        ReminderDetails.CreateReminder -> {
            CreateReminderScreen(
                onUiEvent = viewModel::onUiEvent,
                uiState = uiState,
                reminderNameFocusRequester = reminderNameFocusRequester,
                notificationsPermissionGranted = notificationsPermissionGranted,
                modifier = modifier
            )
        }

        is ReminderDetails.EditReminder -> {
            EditReminderScreen(
                onUiEvent = viewModel::onUiEvent,
                uiState = uiState,
                reminderNameFocusRequester = reminderNameFocusRequester,
                notificationsPermissionGranted = notificationsPermissionGranted,
                modifier = modifier
            )
        }

        ReminderDetails.Initial -> {}
    }
}

@Composable
private fun CreateReminderScreen(
    onUiEvent: (ReminderUiEvent) -> Unit,
    uiState: ReminderUiState,
    reminderNameFocusRequester: FocusRequester,
    notificationsPermissionGranted: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Создание напоминания",
                onBackIconClick = { onUiEvent(ReminderUiEvent.OnBackIconClick) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Screen(
            onUiEvent = onUiEvent,
            uiState = uiState,
            reminderNameFocusRequester = reminderNameFocusRequester,
            notificationsPermissionGranted = notificationsPermissionGranted,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun EditReminderScreen(
    onUiEvent: (ReminderUiEvent) -> Unit,
    uiState: ReminderUiState,
    reminderNameFocusRequester: FocusRequester,
    notificationsPermissionGranted: Boolean,
    modifier: Modifier = Modifier
) {
    val details = uiState.details as ReminderDetails.EditReminder

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Редактирование напоминания",
                onBackIconClick = { onUiEvent(ReminderUiEvent.OnBackIconClick) },
                actions = {
                    IconButton(onClick = { onUiEvent(ReminderUiEvent.OnDeleteIconClick) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Screen(
            onUiEvent = onUiEvent,
            uiState = uiState,
            reminderNameFocusRequester = reminderNameFocusRequester,
            notificationsPermissionGranted = notificationsPermissionGranted,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )

        if (details.showDeleteReminderDialog) {
            ConfirmationDialog(
                text = "Вы действительно хотите удлаить напоминание?",
                onConfirm = { onUiEvent(ReminderUiEvent.OnConfirmDeleteReminderDialog) },
                onDismiss = { onUiEvent(ReminderUiEvent.OnDismissDeleteReminderDialog) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    onUiEvent: (ReminderUiEvent) -> Unit,
    uiState: ReminderUiState,
    reminderNameFocusRequester: FocusRequester,
    notificationsPermissionGranted: Boolean,
    modifier: Modifier = Modifier
) {
    val periodicity = when (uiState.periodicity) {
        Periodicity.ONCE -> "Один раз"
        Periodicity.DAY -> "Каждый день"
        Periodicity.WEEK -> "Каждую неделю"
        Periodicity.MONTH -> "Каждый месяц"
        Periodicity.YEAR -> "Каждый год"
    }

    val date = Date(uiState.selectedDate)
    val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)

    val hours = if (uiState.selectedTime.first < 10) "0${uiState.selectedTime.first}" else "${uiState.selectedTime.first}"
    val minutes = if (uiState.selectedTime.second < 10) "0${uiState.selectedTime.second}" else "${uiState.selectedTime.second}"

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        FinanceTextField(
            value = uiState.reminderName,
            onValueChange = { onUiEvent(ReminderUiEvent.OnReminderNameChanged(it)) },
            label = "Название",
            isError = uiState.reminderNameError,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(reminderNameFocusRequester)
        )

        PickerWithTitle(
            title = "Периодичность напоминания",
            pickerText = periodicity,
            onPickerClick = { onUiEvent(ReminderUiEvent.OnPeriodicityPickerClick) }
        )

        PickerWithTitle(
            title = "Дата следующего напоминания",
            pickerText = formattedDate,
            onPickerClick = { onUiEvent(ReminderUiEvent.OnDatePickerClick) }
        )

        PickerWithTitle(
            title = "Время напоминания",
            pickerText = "$hours:$minutes",
            onPickerClick = { onUiEvent(ReminderUiEvent.OnTimePickerClick) }
        )

        FinanceTextField(
            value = uiState.comment,
            onValueChange = { onUiEvent(ReminderUiEvent.OnCommentChanged(it)) },
            label = "Комментарий",
            placeholder = "Напишите что-нибудь",
            maxLines = 5,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        SaveButton(
            onClick = {
                onUiEvent(ReminderUiEvent.OnSaveButtonClick(notificationsPermissionGranted))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (uiState.showPeriodicityDialog) {
        PeriodicityDialog(
            onConfirmButtonClick = { onUiEvent(ReminderUiEvent.OnPeriodicitySelected(it)) },
            onDismiss = { onUiEvent(ReminderUiEvent.OnDismissDialog) },
            initialSelectedPeriodicity = uiState.periodicity
        )
    }

    if (uiState.showDatePickerDialog) {
        DatePickerModalDialog(
            onConfirmButtonClick = { onUiEvent(ReminderUiEvent.OnDateSelected(it)) },
            onDismiss = { onUiEvent(ReminderUiEvent.OnDismissDialog) },
            initialSelectedDate = uiState.selectedDate,
            selectableDates = PresentOrFutureSelectableDates
        )
    }

    if (uiState.showTimePickerDialog) {
        TimePickerModalDialog(
            onConfirmButtonClick = { onUiEvent(ReminderUiEvent.OnTimeSelected(it)) },
            onDismiss = { onUiEvent(ReminderUiEvent.OnDismissDialog) },
            initialSelectedTime = uiState.selectedTime
        )
    }

    if (uiState.showReminderNameCollisionDialog) {
        MessageToUserDialog(
            title = "Выберите другое название напоминания",
            message = "Напоминание с таким названием уже существует",
            onConfirm = { onUiEvent(ReminderUiEvent.OnDismissDialog) }
        )
    }

    if (uiState.showInvalidReminderDateDialog) {
        ConfirmationDialog(
            text = "Задана дата в прошлом, напоминание не будет включено. Вы действительно хотите сохранить напоминание?",
            onConfirm = { onUiEvent(ReminderUiEvent.OnInvalidReminderDateConfirm) },
            onDismiss = { onUiEvent(ReminderUiEvent.OnDismissDialog) }
        )
    }
}