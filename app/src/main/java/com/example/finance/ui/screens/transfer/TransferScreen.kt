package com.example.finance.ui.screens.transfer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.ui.common.BackTopBar
import com.example.finance.ui.common.ConfirmationDialog
import com.example.finance.ui.common.DatePickerModalDialog
import com.example.finance.ui.common.FinanceTextField
import com.example.finance.ui.common.MessageToUserDialog
import com.example.finance.ui.common.PickerWithTitle
import com.example.finance.ui.common.SaveButton
import com.example.finance.ui.common.AccountPickerDialog
import com.example.finance.ui.common.PastOrPresentSelectableDates
import com.example.finance.ui.common.SumTextField
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransferScreen(
    navigateBack: () -> Unit,
    viewModel: TransferViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

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

    val transferSumFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                TransferEvent.CloseScreen -> navigateBack()
                TransferEvent.RequestTransferSumFocus -> transferSumFocusRequester.requestFocus()
            }
        }
    }

    when (uiState.details) {
        is TransferDetails.CreateTransfer -> {
            CreateTransferScreen(
                onUiEvent = viewModel::onUiEvent,
                uiState = uiState,
                transferSumFocusRequester = transferSumFocusRequester,
                modifier = modifier
            )
        }

        is TransferDetails.EditTransfer -> {
            EditTransferScreen(
                onUiEvent = viewModel::onUiEvent,
                uiState = uiState,
                transferSumFocusRequester = transferSumFocusRequester,
                modifier = modifier
            )
        }

        TransferDetails.Initial -> {}
    }
}

@Composable
private fun CreateTransferScreen(
    onUiEvent: (TransferUiEvent) -> Unit,
    uiState: TransferUiState,
    transferSumFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Создание перевода",
                onBackIconClick = { onUiEvent(TransferUiEvent.OnBackIconClick) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Screen(
            onUiEvent = onUiEvent,
            uiState = uiState,
            transferSumFocusRequester = transferSumFocusRequester,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun EditTransferScreen(
    onUiEvent: (TransferUiEvent) -> Unit,
    uiState: TransferUiState,
    transferSumFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val details = uiState.details as TransferDetails.EditTransfer

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Редактирование перевода",
                onBackIconClick = { onUiEvent(TransferUiEvent.OnBackIconClick) },
                actions = {
                    IconButton(onClick = { onUiEvent(TransferUiEvent.OnDeleteIconClick) }) {
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
            transferSumFocusRequester = transferSumFocusRequester,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }

    if (details.showDeleteTransferDialog) {
        ConfirmationDialog(
            text = "Вы действительно хотите удалить перевод? Счета придут к состоянию до перевода",
            onConfirm = { onUiEvent(TransferUiEvent.OnConfirmDeleteTransferDialog) },
            onDismiss = { onUiEvent(TransferUiEvent.OnDismissDeleteTransferDialog) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    onUiEvent: (TransferUiEvent) -> Unit,
    uiState: TransferUiState,
    transferSumFocusRequester: FocusRequester,
    modifier: Modifier
) {
    val date = Date(uiState.selectedDate)
    val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            SumTextField(
                sum = uiState.transferSum,
                onValueChange = { onUiEvent(TransferUiEvent.OnTransferSumChanged(it)) },
                isError = uiState.transferSumError,
                modifier = Modifier.focusRequester(transferSumFocusRequester)
            )
        }

        PickerWithTitle(
            title = "Перевод со счета",
            pickerText = uiState.selectedFromAccount?.name ?: "Не выбрано",
            onPickerClick = { onUiEvent(TransferUiEvent.OnFromAccountPickerClick) },
            isError = uiState.fromAccountIdError
        )

        PickerWithTitle(
            title = "Перевод на счет",
            pickerText = uiState.selectedToAccount?.name ?: "Не выбрано",
            onPickerClick = { onUiEvent(TransferUiEvent.OnToAccountPickerClick) },
            isError = uiState.toAccountIdError
        )

        PickerWithTitle(
            title = "Дата",
            pickerText = formattedDate,
            onPickerClick = { onUiEvent(TransferUiEvent.OnDatePickerClick) }
        )

        FinanceTextField(
            value = uiState.comment,
            onValueChange = { onUiEvent(TransferUiEvent.OnCommentChanged(it)) },
            label = "Комментарий",
            placeholder = "Напишите что-нибудь",
            maxLines = 5,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        SaveButton(
            onClick = { onUiEvent(TransferUiEvent.OnSaveButtonClick) },
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (uiState.showSelectFromAccountDialog) {
        AccountPickerDialog(
            accounts = uiState.accounts,
            initialSelectedAccountId = uiState.selectedFromAccount?.id ?: -1,
            onConfirmButtonClick = { onUiEvent(TransferUiEvent.OnFromAccountSelected(it)) },
            onDismiss = { onUiEvent(TransferUiEvent.OnDismissDialog) }
        )
    }

    if (uiState.showSelectToAccountDialog) {
        AccountPickerDialog(
            accounts = uiState.accounts,
            initialSelectedAccountId = uiState.selectedToAccount?.id ?: -1,
            onConfirmButtonClick = { onUiEvent(TransferUiEvent.OnToAccountSelected(it)) },
            onDismiss = { onUiEvent(TransferUiEvent.OnDismissDialog) }
        )
    }

    if (uiState.showDatePickerDialog) {
        DatePickerModalDialog(
            onConfirmButtonClick = { onUiEvent(TransferUiEvent.OnDateSelected(it)) },
            onDismiss = { onUiEvent(TransferUiEvent.OnDismissDialog) },
            initialSelectedDate = uiState.selectedDate,
            selectableDates = PastOrPresentSelectableDates
        )
    }

    if (uiState.showSelectedAccountsAreSameDialog) {
        MessageToUserDialog(
            title = "Выбранные счета одинаковы",
            message = "Невозможно выполнить перевод на тот же счет. Пожалуйста, выберите другой счет списания или получения",
            onConfirm = { onUiEvent(TransferUiEvent.OnDismissDialog) }
        )
    }
}