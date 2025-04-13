package com.example.finance.ui.screens.reminderlist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.ui.common.AddFloatingActionButton
import com.example.finance.ui.common.MenuTopBar
import com.example.finance.ui.common.MessageToUserDialog
import com.example.finance.ui.screens.reminderlist.components.ReminderList

@Composable
fun ReminderListScreen(
    navigateToCreateReminderScreen: () -> Unit,
    navigateToEditReminderScreen: (Int) -> Unit,
    openNavigationDrawer: () -> Unit,
    viewModel: ReminderListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
    ) { permissionGranted ->
        notificationsPermissionGranted = permissionGranted

        if (!permissionGranted) {
            viewModel.onUiEvent(ReminderListUiEvent.OnPermissionNotGranted)
        } else {
            viewModel.onUiEvent(ReminderListUiEvent.OnPermissionGranted)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                ReminderListEvent.RequestPermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestNotificationsPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            MenuTopBar(
                title = { Text(text = "Напоминания") },
                onMenuIconClick = openNavigationDrawer,
                modifier = Modifier.fillMaxWidth()
            )
        },
        floatingActionButton = { AddFloatingActionButton(onClick = navigateToCreateReminderScreen) }
    ) { paddingValues ->
        if (uiState.reminders.isNotEmpty()) {
            ReminderList(
                reminders = uiState.reminders,
                onReminderClick = navigateToEditReminderScreen,
                onReminderSwitchClick = { reminderId ->
                    viewModel.onUiEvent(
                        ReminderListUiEvent.OnReminderSwitchClick(
                            notificationsPermissionGranted,
                            reminderId
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(text = "Нет напоминаний")
            }
        }
    }

    if (uiState.showPermissionNotGrantedDialog) {
        MessageToUserDialog(
            title = "Разрешение на отправку уведомлений не предоставлено",
            message = "Вы можете предоставить разрешение в настройках Вашего устройства",
            onConfirm = { viewModel.onUiEvent(ReminderListUiEvent.OnDismissDialog) }
        )
    }

    if (uiState.showInvalidReminderDateDialog) {
        MessageToUserDialog(
            title = "Время напоминания прошло",
            message = "Отредактируйте дату и время напоминания, чтобы включить его",
            onConfirm = { viewModel.onUiEvent(ReminderListUiEvent.OnDismissDialog) }
        )
    }
}